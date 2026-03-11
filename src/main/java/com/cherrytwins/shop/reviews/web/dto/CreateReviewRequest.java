package com.cherrytwins.shop.reviews.web.dto;

import jakarta.validation.constraints.*;

public class CreateReviewRequest {

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    @Size(max = 120)
    private String title;

    @Size(max = 5000)
    private String comment;

    // opcional (por default true)
    private Boolean isPublic;

    public Integer getRating() { return rating; }
    public String getTitle() { return title; }
    public String getComment() { return comment; }
    public Boolean getIsPublic() { return isPublic; }

    public void setRating(Integer rating) { this.rating = rating; }
    public void setTitle(String title) { this.title = title; }
    public void setComment(String comment) { this.comment = comment; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}