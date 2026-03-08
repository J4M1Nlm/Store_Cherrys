package com.cherrytwins.shop.catalog.web;

import com.cherrytwins.shop.catalog.service.CatalogService;
import com.cherrytwins.shop.catalog.web.dto.*;
import com.cherrytwins.shop.common.pagination.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Catalog (Public)", description = "Endpoints públicos para storefront")
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;
    public CatalogController(CatalogService catalogService) { this.catalogService = catalogService; }

    @Operation(summary = "Listar artistas")
    @GetMapping("/artists")
    public List<ArtistResponse> artists() { return catalogService.listArtists(); }

    @Operation(summary = "Listar categorías (flat)")
    @GetMapping("/categories")
    public List<CategoryResponse> categories() { return catalogService.listCategories(); }

    @Operation(summary = "Árbol de categorías")
    @GetMapping("/categories/tree")
    public List<CategoryTreeResponse> categoryTree() { return catalogService.categoryTree(); }

    @Operation(summary = "Listar productos", description = "Paginación + filtros (q, artist slug, category slug, price range, sort).")
    @GetMapping("/products")
    public PageResponse<ProductListItemResponse> products(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPriceCents,
            @RequestParam(required = false) Integer maxPriceCents,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String sort
    ) {
        return catalogService.listProducts(q, artist, category, minPriceCents, maxPriceCents, page, size, sort);
    }

    @Operation(summary = "Detalle de producto por slug", description = "Incluye imágenes y variantes activas.")
    @GetMapping("/products/{slug}")
    public ProductDetailResponse product(@PathVariable String slug) {
        return catalogService.productDetail(slug);
    }
}