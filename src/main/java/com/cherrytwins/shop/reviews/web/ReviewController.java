package com.cherrytwins.shop.reviews.web;

import com.cherrytwins.shop.reviews.service.ReviewService;
import com.cherrytwins.shop.reviews.web.dto.*;
import com.cherrytwins.shop.security.CurrentUser;
import com.cherrytwins.shop.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reviews", description = "Reseñas por producto (público + customer)")
@RestController
@RequestMapping("/api/catalog/products")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Listar reseñas públicas por producto")
    @GetMapping("/{productId}/reviews")
    public Page<ReviewResponse> publicReviews(@PathVariable Long productId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return reviewService.publicReviews(productId, page, size);
    }

    @Operation(summary = "Crear reseña (requiere compra)", description = "Crea una reseña del usuario autenticado para el producto.")
    @PostMapping("/{productId}/reviews")
    public ReviewResponse create(@CurrentUser UserPrincipal principal,
                                 @PathVariable Long productId,
                                 @Valid @RequestBody CreateReviewRequest req) {
        return reviewService.create(principal.getId(), productId, req);
    }

    @Operation(summary = "Actualizar mi reseña", description = "Actualiza rating/title/comment. Solo dueño.")
    @PutMapping("/reviews/{reviewId}")
    public ReviewResponse updateMine(@CurrentUser UserPrincipal principal,
                                     @PathVariable Long reviewId,
                                     @Valid @RequestBody UpdateReviewRequest req) {
        return reviewService.updateMine(principal.getId(), reviewId, req);
    }

    @Operation(summary = "Eliminar mi reseña", description = "Elimina la reseña del usuario autenticado.")
    @DeleteMapping("/reviews/{reviewId}")
    public void deleteMine(@CurrentUser UserPrincipal principal,
                           @PathVariable Long reviewId) {
        reviewService.deleteMine(principal.getId(), reviewId);
    }
}