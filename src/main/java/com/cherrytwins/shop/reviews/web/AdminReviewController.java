package com.cherrytwins.shop.reviews.web;

import com.cherrytwins.shop.reviews.service.ReviewService;
import com.cherrytwins.shop.reviews.web.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reviews (Admin)", description = "Moderación de reseñas")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Listar reseñas por producto (incluye privadas)")
    @GetMapping
    public Page<ReviewResponse> list(@RequestParam Long productId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "50") int size) {
        return reviewService.adminReviews(productId, page, size);
    }

    @Operation(summary = "Cambiar visibilidad (is_public)")
    @PutMapping("/{reviewId}/public")
    public ReviewResponse setPublic(@PathVariable Long reviewId, @RequestParam boolean value) {
        return reviewService.setPublic(reviewId, value);
    }

    @Operation(summary = "Eliminar reseña (admin)")
    @DeleteMapping("/{reviewId}")
    public void delete(@PathVariable Long reviewId) {
        reviewService.deleteAdmin(reviewId);
    }
}