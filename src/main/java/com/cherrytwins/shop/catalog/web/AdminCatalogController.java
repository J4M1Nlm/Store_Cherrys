package com.cherrytwins.shop.catalog.web;

import com.cherrytwins.shop.catalog.service.AdminCatalogService;
import com.cherrytwins.shop.catalog.web.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Catalog (Admin)", description = "CRUD admin para catálogo")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/catalog")
public class AdminCatalogController {

    private final AdminCatalogService adminCatalogService;
    public AdminCatalogController(AdminCatalogService adminCatalogService) { this.adminCatalogService = adminCatalogService; }

    @Operation(summary = "Crear artista")
    @PostMapping("/artists")
    public ArtistResponse createArtist(@Valid @RequestBody AdminArtistRequest req) {
        return adminCatalogService.createArtist(req);
    }

    @Operation(summary = "Actualizar artista")
    @PutMapping("/artists/{id}")
    public ArtistResponse updateArtist(@PathVariable Long id, @Valid @RequestBody AdminArtistRequest req) {
        return adminCatalogService.updateArtist(id, req);
    }

    @Operation(summary = "Eliminar artista")
    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        adminCatalogService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Crear categoría")
    @PostMapping("/categories")
    public CategoryResponse createCategory(@Valid @RequestBody AdminCategoryRequest req) {
        return adminCatalogService.createCategory(req);
    }

    @Operation(summary = "Actualizar categoría")
    @PutMapping("/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody AdminCategoryRequest req) {
        return adminCatalogService.updateCategory(id, req);
    }

    @Operation(summary = "Eliminar categoría")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminCatalogService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Crear producto")
    @PostMapping("/products")
    public ProductDetailResponse createProduct(@Valid @RequestBody AdminProductRequest req) {
        return adminCatalogService.createProduct(req);
    }

    @Operation(summary = "Actualizar producto")
    @PutMapping("/products/{id}")
    public ProductDetailResponse updateProduct(@PathVariable Long id, @Valid @RequestBody AdminProductRequest req) {
        return adminCatalogService.updateProduct(id, req);
    }

    @Operation(summary = "Activar/desactivar producto")
    @PutMapping("/products/{id}/active")
    public ProductDetailResponse setActive(@PathVariable Long id, @RequestParam boolean value) {
        return adminCatalogService.setProductActive(id, value);
    }

    @Operation(summary = "Eliminar producto")
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminCatalogService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Agregar imagen a producto")
    @PostMapping("/products/{productId}/images")
    public List<ProductImageResponse> addImage(@PathVariable Long productId, @Valid @RequestBody AdminProductImageRequest req) {
        return adminCatalogService.addProductImage(productId, req);
    }

    @Operation(summary = "Eliminar imagen de producto")
    @DeleteMapping("/products/{productId}/images/{imageId}")
    public List<ProductImageResponse> deleteImage(@PathVariable Long productId, @PathVariable Long imageId) {
        return adminCatalogService.deleteProductImage(productId, imageId);
    }

    @Operation(summary = "Agregar variante a producto")
    @PostMapping("/products/{productId}/variants")
    public List<ProductVariantResponse> addVariant(@PathVariable Long productId, @Valid @RequestBody AdminProductVariantRequest req) {
        return adminCatalogService.addVariant(productId, req);
    }

    @Operation(summary = "Actualizar variante de producto")
    @PutMapping("/products/{productId}/variants/{variantId}")
    public List<ProductVariantResponse> updateVariant(@PathVariable Long productId, @PathVariable Long variantId,
                                                      @Valid @RequestBody AdminProductVariantRequest req) {
        return adminCatalogService.updateVariant(productId, variantId, req);
    }

    @Operation(summary = "Eliminar variante de producto")
    @DeleteMapping("/products/{productId}/variants/{variantId}")
    public List<ProductVariantResponse> deleteVariant(@PathVariable Long productId, @PathVariable Long variantId) {
        return adminCatalogService.deleteVariant(productId, variantId);
    }
}