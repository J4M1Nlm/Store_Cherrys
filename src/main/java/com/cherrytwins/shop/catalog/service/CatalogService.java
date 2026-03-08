package com.cherrytwins.shop.catalog.service;

import com.cherrytwins.shop.catalog.domain.*;
import com.cherrytwins.shop.catalog.repository.*;
import com.cherrytwins.shop.catalog.web.dto.*;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.common.pagination.PageResponse;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final ArtistRepository artistRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;

    public CatalogService(ArtistRepository artistRepository,
                          CategoryRepository categoryRepository,
                          ProductRepository productRepository,
                          ProductImageRepository productImageRepository,
                          ProductVariantRepository productVariantRepository) {
        this.artistRepository = artistRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productVariantRepository = productVariantRepository;
    }

    // -------- Public: Artists
    public List<ArtistResponse> listArtists() {
        return artistRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(a -> new ArtistResponse(a.getId(), a.getName(), a.getSlug(), a.getBio()))
                .toList();
    }

    // -------- Public: Categories (flat)
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentId()))
                .toList();
    }

    // -------- Public: Categories (tree)
    public List<CategoryTreeResponse> categoryTree() {
        List<Category> all = categoryRepository.findAll();
        Map<Long, CategoryTreeResponse> map = new HashMap<>();
        for (Category c : all) {
            map.put(c.getId(), new CategoryTreeResponse(c.getId(), c.getName(), c.getSlug(), c.getParentId()));
        }
        List<CategoryTreeResponse> roots = new ArrayList<>();
        for (Category c : all) {
            CategoryTreeResponse node = map.get(c.getId());
            if (c.getParentId() == null) roots.add(node);
            else {
                CategoryTreeResponse parent = map.get(c.getParentId());
                if (parent != null) parent.addChild(node);
                else roots.add(node);
            }
        }
        // Ordenar por name para UI
        sortTreeByName(roots);
        return roots;
    }

    private void sortTreeByName(List<CategoryTreeResponse> nodes) {
        nodes.sort(Comparator.comparing(CategoryTreeResponse::getName, String.CASE_INSENSITIVE_ORDER));
        for (CategoryTreeResponse n : nodes) sortTreeByName(n.getChildren());
    }

    // -------- Public: Products list (paged + filters)
    public PageResponse<ProductListItemResponse> listProducts(
            String q,
            String artistSlug,
            String categorySlug,
            Integer minPriceCents,
            Integer maxPriceCents,
            Integer page,
            Integer size,
            String sort
    ) {
        Long artistId = null;
        if (artistSlug != null && !artistSlug.isBlank()) {
            artistId = artistRepository.findBySlug(artistSlug)
                    .map(Artist::getId)
                    .orElseThrow(() -> new NotFoundException("Artist not found"));
        }

        Long categoryId = null;
        if (categorySlug != null && !categorySlug.isBlank()) {
            categoryId = categoryRepository.findBySlug(categorySlug)
                    .map(Category::getId)
                    .orElseThrow(() -> new NotFoundException("Category not found"));
        }

        Sort s = parseSort(sort);
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size, s);

        Specification<Product> spec = Specification.where(ProductSpecifications.activeOnly(true))
                .and(ProductSpecifications.nameOrSlugContains(q))
                .and(ProductSpecifications.byArtistId(artistId))
                .and(ProductSpecifications.byCategoryId(categoryId))
                .and(ProductSpecifications.priceBetween(minPriceCents, maxPriceCents));

        Page<Product> products = productRepository.findAll(spec, pageable);

        // Para no hacer N+1 por imagen principal, precargamos imágenes por productId
        List<Long> ids = products.getContent().stream().map(Product::getId).toList();
        Map<Long, String> mainImage = new HashMap<>();
        if (!ids.isEmpty()) {
            // estrategia simple: por cada producto buscamos su primera imagen ordenada (podría optimizarse con query nativa luego)
            for (Long pid : ids) {
                List<ProductImage> imgs = productImageRepository.findAllByProductIdOrderBySortOrderAscIdAsc(pid);
                mainImage.put(pid, imgs.isEmpty() ? null : imgs.get(0).getUrl());
            }
        }

        // map slugs (artist/category)
        Map<Long, String> artistSlugMap = artistRepository.findAllById(
                products.getContent().stream().map(Product::getArtistId).filter(Objects::nonNull).distinct().toList()
        ).stream().collect(Collectors.toMap(Artist::getId, Artist::getSlug));

        Map<Long, String> categorySlugMap = categoryRepository.findAllById(
                products.getContent().stream().map(Product::getCategoryId).filter(Objects::nonNull).distinct().toList()
        ).stream().collect(Collectors.toMap(Category::getId, Category::getSlug));

        Page<ProductListItemResponse> mapped = products.map(p -> new ProductListItemResponse(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.isActive(),
                p.getBasePriceCents(),
                p.getCurrency(),
                mainImage.get(p.getId()),
                p.getArtistId() == null ? null : artistSlugMap.get(p.getArtistId()),
                p.getCategoryId() == null ? null : categorySlugMap.get(p.getCategoryId())
        ));

        return PageResponse.from(mapped);
    }

    private Sort parseSort(String sort) {
        // sort=createdAt_desc | price_asc | price_desc | name_asc
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "createdAt");
        return switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "basePriceCents");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "basePriceCents");
            case "name_asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "createdAt_asc" -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    // -------- Public: Product detail (slug)
    public ProductDetailResponse productDetail(String slug) {
        Product p = productRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (!p.isActive()) throw new NotFoundException("Product not found");

        ArtistResponse artist = null;
        if (p.getArtistId() != null) {
            Artist a = artistRepository.findById(p.getArtistId()).orElse(null);
            if (a != null) artist = new ArtistResponse(a.getId(), a.getName(), a.getSlug(), a.getBio());
        }

        CategoryResponse category = null;
        if (p.getCategoryId() != null) {
            Category c = categoryRepository.findById(p.getCategoryId()).orElse(null);
            if (c != null) category = new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentId());
        }

        List<ProductImageResponse> images = productImageRepository.findAllByProductIdOrderBySortOrderAscIdAsc(p.getId())
                .stream().map(i -> new ProductImageResponse(i.getId(), i.getUrl(), i.getAltText(), i.getSortOrder()))
                .toList();

        List<ProductVariantResponse> variants = productVariantRepository.findAllByProductIdAndActiveTrueOrderByIdAsc(p.getId())
                .stream().map(v -> new ProductVariantResponse(
                        v.getId(), v.getSku(), v.getVariantName(), v.getAttributes(),
                        v.getPriceCents(), v.getCurrency(), v.getStockOnHand(), v.isActive()
                )).toList();

        return new ProductDetailResponse(
                p.getId(), p.getName(), p.getSlug(), p.getDescription(), p.isActive(),
                p.getBasePriceCents(), p.getCurrency(),
                artist, category, images, variants
        );
    }
}