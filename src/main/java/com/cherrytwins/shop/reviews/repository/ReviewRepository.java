package com.cherrytwins.shop.reviews.repository;

import com.cherrytwins.shop.reviews.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByProductIdAndIsPublicTrueOrderByCreatedAtDesc(Long productId, Pageable pageable);

    Page<Review> findAllByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    Optional<Review> findByProductIdAndUserId(Long productId, Long userId);

    Optional<Review> findByIdAndUserId(Long id, Long userId);

    // Admin filters could be added later (publicOnly/userId/etc.)


}