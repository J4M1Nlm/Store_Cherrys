package com.cherrytwins.shop.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminProductRequest {

    @NotBlank @Size(max = 255)
    private String name;

    @Size(max = 280)
    private String slug;

    private String description;

    private Boolean active;

    @NotNull
    private Integer basePriceCents;

    @NotBlank @Size(min = 3, max = 3)
    private String currency;

    private Long artistId;
    private Long categoryId;

    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public Boolean getActive() { return active; }
    public Integer getBasePriceCents() { return basePriceCents; }
    public String getCurrency() { return currency; }
    public Long getArtistId() { return artistId; }
    public Long getCategoryId() { return categoryId; }

    public void setName(String name) { this.name = name; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setDescription(String description) { this.description = description; }
    public void setActive(Boolean active) { this.active = active; }
    public void setBasePriceCents(Integer basePriceCents) { this.basePriceCents = basePriceCents; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}