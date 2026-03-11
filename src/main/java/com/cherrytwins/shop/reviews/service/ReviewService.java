package com.cherrytwins.shop.reviews.service;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.orders.repository.OrderPurchaseRepository;
import com.cherrytwins.shop.reviews.domain.Review;
import com.cherrytwins.shop.reviews.repository.ReviewRepository;
import com.cherrytwins.shop.reviews.web.dto.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderPurchaseRepository purchaseRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderPurchaseRepository purchaseRepository) {
        this.reviewRepository = reviewRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public Page<ReviewResponse> publicReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAllByProductIdAndIsPublicTrueOrderByCreatedAtDesc(productId, pageable)
                .map(this::toResponse);
    }

    public Page<ReviewResponse> adminReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAllByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(this::toResponse);
    }

    public ReviewResponse myReview(Long userId, Long productId) {
        Review r = reviewRepository.findByProductIdAndUserId(productId, userId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        return toResponse(r);
    }

    @Transactional
    public ReviewResponse create(Long userId, Long productId, CreateReviewRequest req) {
        // 1 review por user/product (regla de negocio)
        reviewRepository.findByProductIdAndUserId(productId, userId).ifPresent(existing -> {
            throw new BadRequestException("You already reviewed this product");
        });

        // Regla real: solo si compró (PAID o FULFILLED)
        boolean purchased = purchaseRepository.hasPurchasedProduct(userId, productId);
        if (!purchased) throw new BadRequestException("You can review only after purchasing this product");

        Review r = new Review();
        r.setUserId(userId);
        r.setProductId(productId);
        r.setRating(req.getRating());
        r.setTitle(req.getTitle());
        r.setComment(req.getComment());
        r.setPublic(req.getIsPublic() == null ? true : req.getIsPublic());

        r = reviewRepository.save(r);
        return toResponse(r);
    }

    @Transactional
    public ReviewResponse updateMine(Long userId, Long reviewId, UpdateReviewRequest req) {
        Review r = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        r.setRating(req.getRating());
        r.setTitle(req.getTitle());
        r.setComment(req.getComment());
        if (req.getIsPublic() != null) r.setPublic(req.getIsPublic());

        return toResponse(r);
    }

    @Transactional
    public void deleteMine(Long userId, Long reviewId) {
        Review r = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        reviewRepository.delete(r);
    }

    // Admin moderation
    @Transactional
    public ReviewResponse setPublic(Long reviewId, boolean value) {
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        r.setPublic(value);
        return toResponse(r);
    }

    @Transactional
    public void deleteAdmin(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) throw new NotFoundException("Review not found");
        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getProductId(),
                r.getUserId(),
                r.getRating(),
                r.getTitle(),
                r.getComment(),
                r.isPublic(),
                r.getCreatedAt()
        );
    }
}