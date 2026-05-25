package com.cherrytwins.shop.catalog.service;

import com.cherrytwins.shop.catalog.domain.*;
import com.cherrytwins.shop.catalog.repository.*;
import com.cherrytwins.shop.catalog.web.dto.*;
import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.common.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminCatalogService {

    private final ArtistRepository artistRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;

    public AdminCatalogService(ArtistRepository artistRepository,
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

    // -------- Artist
    @Transactional
    public ArtistResponse createArtist(AdminArtistRequest req) {
        String slug = (req.getSlug() == null || req.getSlug().isBlank())
                ? SlugUtil.toSlug(req.getName())
                : SlugUtil.toSlug(req.getSlug());

        if (artistRepository.existsBySlug(slug)) {
            throw new BadRequestException("Artist slug already exists: " + slug);
        }

        Artist a = new Artist();
        a.setName(req.getName());
        a.setSlug(slug);
        a.setBio(req.getBio());

        a = artistRepository.save(a);
        return new ArtistResponse(a.getId(), a.getName(), a.getSlug(), a.getBio());
    }

    @Transactional
    public ArtistResponse updateArtist(Long id, AdminArtistRequest req) {
        Artist a = artistRepository.findById(id).orElseThrow(() -> new NotFoundException("Artist not found"));

        String slug = (req.getSlug() == null || req.getSlug().isBlank())
                ? SlugUtil.toSlug(req.getName())
                : SlugUtil.toSlug(req.getSlug());

        if (!slug.equals(a.getSlug()) && artistRepository.existsBySlug(slug)) {
            throw new BadRequestException("Artist slug already exists: " + slug);
        }

        a.setName(req.getName());
        a.setSlug(slug);
        a.setBio(req.getBio());

        return new ArtistResponse(a.getId(), a.getName(), a.getSlug(), a.getBio());
    }

    @Transactional
    public void deleteArtist(Long id) {
        // Si hay productos referenciándolo, tu FK es ON DELETE SET NULL, así que es seguro.
        artistRepository.deleteById(id);
    }

    // -------- Category
    @Transactional
    public CategoryResponse createCategory(AdminCategoryRequest req) {
        String slug = (req.getSlug() == null || req.getSlug().isBlank())
                ? SlugUtil.toSlug(req.getName())
                : SlugUtil.toSlug(req.getSlug());

        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Category slug already exists: " + slug);
        }

        if (req.getParentId() != null && !categoryRepository.existsById(req.getParentId())) {
            throw new BadRequestException("Parent category not found");
        }

        Category c = new Category();
        c.setName(req.getName());
        c.setSlug(slug);
        c.setParentId(req.getParentId());

        c = categoryRepository.save(c);
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentId());
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, AdminCategoryRequest req) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));

        String slug = (req.getSlug() == null || req.getSlug().isBlank())
                ? SlugUtil.toSlug(req.getName())
                : SlugUtil.toSlug(req.getSlug());

        if (!slug.equals(c.getSlug()) && categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Category slug already exists: " + slug);
        }

        if (req.getParentId() != null) {
            if (req.getParentId().equals(id)) throw new BadRequestException("Category cannot be its own parent");
            if (!categoryRepository.existsById(req.getParentId())) throw new BadRequestException("Parent category not found");
        }

        c.setName(req.getName());
        c.setSlug(slug);
        c.setParentId(req.getParentId());

        return new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentId());
    }

    @Transactional
    public void deleteCategory(Long id) {
        // products FK is ON DELETE SET NULL, categories parent FK ON DELETE SET NULL
        categoryRepository.deleteById(id);
    }

    // -------- Product
    @Transactional
    public ProductDetailResponse createProduct(AdminProductRequest req) {
        validateMoney(req.getBasePriceCents());

        String slug = (req.getSlug() == null || req.getSlug().isBlank())
                ? SlugUtil.toSlug(req.getName())
                : SlugUtil.toSlug(req.getSlug());

        if (productRepository.existsBySlug(slug)) {
            throw new BadRequestException("Product slug already exists: " + slug);
        }

        if (req.getArtistId() != null && !artistRepository.existsById(req.getArtistId())) {
            throw new BadRequestException("Artist not found");
        }
        if (req.getCategoryId() != null && !categoryRepository.existsById(req.getCategoryId())) {
            throw new BadRequestException("Category not found");
        }

        Product p = new Product();
        p.setName(req.getName());
        p.setSlug(slug);
        p.setDescription(req.getDescription());
        p.setActive(req.getActive() == null ? true : req.getActive());
        p.setBasePriceCents(req.getBasePriceCents());
        p.setCurrency(req.getCurrency().toUpperCase());
        p.setArtistId(req.getArtistId());
        p.setCategoryId(req.getCategoryId());

        p = productRepository.save(p);

        // inicialmente sin imágenes/variantes
        return buildAdminDetail(p);
    }

    @Transactional
    public ProductDetailResponse updateProduct(Long id, AdminProductRequest req) {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));

        validateMoney(req.getBasePriceCents());

        String slug = (req.getSlug() == null || req.getSlug().isBlank())
                ? SlugUtil.toSlug(req.getName())
                : SlugUtil.toSlug(req.getSlug());

        if (!slug.equals(p.getSlug()) && productRepository.existsBySlug(slug)) {
            throw new BadRequestException("Product slug already exists: " + slug);
        }

        if (req.getArtistId() != null && !artistRepository.existsById(req.getArtistId())) {
            throw new BadRequestException("Artist not found");
        }
        if (req.getCategoryId() != null && !categoryRepository.existsById(req.getCategoryId())) {
            throw new BadRequestException("Category not found");
        }

        p.setName(req.getName());
        p.setSlug(slug);
        p.setDescription(req.getDescription());
        if (req.getActive() != null) p.setActive(req.getActive());
        p.setBasePriceCents(req.getBasePriceCents());
        p.setCurrency(req.getCurrency().toUpperCase());
        p.setArtistId(req.getArtistId());
        p.setCategoryId(req.getCategoryId());

        return buildAdminDetail(p);
    }

    @Transactional
    public void deleteProduct(Long id) {
        // cleanup children manually to avoid leaving orphans if FK isn't cascade everywhere
        productImageRepository.deleteAllByProductId(id);
        List<ProductVariant> variants = productVariantRepository.findAllByProductIdOrderByIdAsc(id);
        productVariantRepository.deleteAll(variants);
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public com.cherrytwins.shop.common.pagination.PageResponse<com.cherrytwins.shop.catalog.web.dto.ProductListItemResponse> listProducts(int page, int size, String sort) {
        org.springframework.data.domain.Sort s = parseSort(sort);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page < 0 ? 0 : page, size <= 0 ? 20 : size, s);

        org.springframework.data.domain.Page<Product> products = productRepository.findAll(pageable);

        List<Long> ids = products.getContent().stream().map(Product::getId).toList();
        Map<Long, String> mainImage = new HashMap<>();
        if (!ids.isEmpty()) {
            for (Long pid : ids) {
                List<ProductImage> imgs = productImageRepository.findAllByProductIdOrderBySortOrderAscIdAsc(pid);
                mainImage.put(pid, imgs.isEmpty() ? null : imgs.get(0).getUrl());
            }
        }

        Map<Long, String> artistSlugMap = artistRepository.findAllById(
                products.getContent().stream().map(Product::getArtistId).filter(Objects::nonNull).distinct().toList()
        ).stream().collect(Collectors.toMap(Artist::getId, Artist::getSlug));

        Map<Long, String> categorySlugMap = categoryRepository.findAllById(
                products.getContent().stream().map(Product::getCategoryId).filter(Objects::nonNull).distinct().toList()
        ).stream().collect(Collectors.toMap(Category::getId, Category::getSlug));

        org.springframework.data.domain.Page<com.cherrytwins.shop.catalog.web.dto.ProductListItemResponse> mapped = products.map(p -> new com.cherrytwins.shop.catalog.web.dto.ProductListItemResponse(
                p.getId(), p.getName(), p.getSlug(), p.isActive(), p.getBasePriceCents(), p.getCurrency(),
                mainImage.get(p.getId()), p.getArtistId() == null ? null : artistSlugMap.get(p.getArtistId()),
                p.getCategoryId() == null ? null : categorySlugMap.get(p.getCategoryId())
        ));

        return com.cherrytwins.shop.common.pagination.PageResponse.from(mapped);
    }

    @Transactional(readOnly = true)
    public com.cherrytwins.shop.catalog.web.dto.ProductDetailResponse getProduct(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        return buildAdminDetail(p);
    }

    @Transactional(readOnly = true)
    public List<com.cherrytwins.shop.catalog.web.dto.ProductVariantResponse> listProductVariants(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        return productVariantRepository.findAllByProductIdOrderByIdAsc(productId)
                .stream().map(this::toVariantResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<com.cherrytwins.shop.catalog.web.dto.CategoryResponse> listCategories() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream().map(c -> new com.cherrytwins.shop.catalog.web.dto.CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public com.cherrytwins.shop.catalog.web.dto.CategoryResponse getCategory(Long id) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        return new com.cherrytwins.shop.catalog.web.dto.CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentId());
    }

    private org.springframework.data.domain.Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
        return switch (sort) {
            case "price_asc" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "basePriceCents");
            case "price_desc" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "basePriceCents");
            case "name_asc" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "name");
            case "name_desc" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "name");
            case "createdAt_asc" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "createdAt");
            default -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
        };
    }

    @Transactional
    public ProductDetailResponse setProductActive(Long id, boolean value) {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        p.setActive(value);
        return buildAdminDetail(p);
    }

    // -------- Images
    @Transactional
    public List<ProductImageResponse> addProductImage(Long productId, AdminProductImageRequest req) {
        Product p = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));

        ProductImage img = new ProductImage();
        img.setProductId(p.getId());
        img.setUrl(req.getUrl());
        img.setAltText(req.getAltText());
        img.setSortOrder(req.getSortOrder());

        productImageRepository.save(img);
        return productImageRepository.findAllByProductIdOrderBySortOrderAscIdAsc(productId)
                .stream().map(i -> new ProductImageResponse(i.getId(), i.getUrl(), i.getAltText(), i.getSortOrder()))
                .toList();
    }

    @Transactional
    public List<ProductImageResponse> deleteProductImage(Long productId, Long imageId) {
        Product p = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        ProductImage img = productImageRepository.findById(imageId).orElseThrow(() -> new NotFoundException("Image not found"));
        if (!img.getProductId().equals(p.getId())) throw new BadRequestException("Image does not belong to product");
        productImageRepository.delete(img);

        return productImageRepository.findAllByProductIdOrderBySortOrderAscIdAsc(productId)
                .stream().map(i -> new ProductImageResponse(i.getId(), i.getUrl(), i.getAltText(), i.getSortOrder()))
                .toList();
    }

    // -------- Variants
    @Transactional
    public List<ProductVariantResponse> addVariant(Long productId, AdminProductVariantRequest req) {
        Product p = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        if (productVariantRepository.existsBySku(req.getSku())) throw new BadRequestException("SKU already exists");

        validateMoney(req.getPriceCents());
        validateStock(req.getStockOnHand());

        ProductVariant v = new ProductVariant();
        v.setProductId(p.getId());
        v.setSku(req.getSku());
        v.setVariantName(req.getVariantName());
        v.setAttributes(req.getAttributes());
        v.setPriceCents(req.getPriceCents());
        v.setCurrency(req.getCurrency().toUpperCase());
        v.setStockOnHand(req.getStockOnHand());
        v.setActive(req.getActive() == null ? true : req.getActive());

        productVariantRepository.save(v);
        return productVariantRepository.findAllByProductIdOrderByIdAsc(productId)
                .stream().map(this::toVariantResponse).toList();
    }

    @Transactional
    public List<ProductVariantResponse> updateVariant(Long productId, Long variantId, AdminProductVariantRequest req) {
        productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        ProductVariant v = productVariantRepository.findById(variantId).orElseThrow(() -> new NotFoundException("Variant not found"));
        if (!v.getProductId().equals(productId)) throw new BadRequestException("Variant does not belong to product");

        // SKU change?
        if (!req.getSku().equals(v.getSku()) && productVariantRepository.existsBySku(req.getSku())) {
            throw new BadRequestException("SKU already exists");
        }

        validateMoney(req.getPriceCents());
        validateStock(req.getStockOnHand());

        v.setSku(req.getSku());
        v.setVariantName(req.getVariantName());
        v.setAttributes(req.getAttributes());
        v.setPriceCents(req.getPriceCents());
        v.setCurrency(req.getCurrency().toUpperCase());
        v.setStockOnHand(req.getStockOnHand());
        if (req.getActive() != null) v.setActive(req.getActive());

        return productVariantRepository.findAllByProductIdOrderByIdAsc(productId)
                .stream().map(this::toVariantResponse).toList();
    }

    @Transactional
    public List<ProductVariantResponse> deleteVariant(Long productId, Long variantId) {
        productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        ProductVariant v = productVariantRepository.findById(variantId).orElseThrow(() -> new NotFoundException("Variant not found"));
        if (!v.getProductId().equals(productId)) throw new BadRequestException("Variant does not belong to product");
        productVariantRepository.delete(v);

        return productVariantRepository.findAllByProductIdOrderByIdAsc(productId)
                .stream().map(this::toVariantResponse).toList();
    }

    // -------- helpers
    private ProductDetailResponse buildAdminDetail(Product p) {
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

        List<ProductVariantResponse> variants = productVariantRepository.findAllByProductIdOrderByIdAsc(p.getId())
                .stream().map(this::toVariantResponse)
                .toList();

        return new ProductDetailResponse(
                p.getId(), p.getName(), p.getSlug(), p.getDescription(), p.isActive(),
                p.getBasePriceCents(), p.getCurrency(),
                artist, category, images, variants
        );
    }

    private ProductVariantResponse toVariantResponse(ProductVariant v) {
        return new ProductVariantResponse(
                v.getId(), v.getSku(), v.getVariantName(), v.getAttributes(),
                v.getPriceCents(), v.getCurrency(), v.getStockOnHand(), v.isActive()
        );
    }

    private void validateMoney(Integer cents) {
        if (cents == null || cents < 0) throw new BadRequestException("Money value must be >= 0");
    }

    private void validateStock(Integer stock) {
        if (stock == null || stock < 0) throw new BadRequestException("Stock must be >= 0");
    }
}
