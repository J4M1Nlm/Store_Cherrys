package com.cherrytwins.shop.reviews.web.dto;

import java.time.OffsetDateTime;

public class ReviewResponse {
    private Long id;
    private Long productId;
    private Long userId;
    private int rating;
    private String title;
    private String comment;
    private boolean isPublic;
    private OffsetDateTime createdAt;

    public ReviewResponse(Long id, Long productId, Long userId, int rating, String title,
                          String comment, boolean isPublic, OffsetDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getUserId() { return userId; }
    public int getRating() { return rating; }
    public String getTitle() { return title; }
    public String getComment() { return comment; }
    public boolean isPublic() { return isPublic; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}