package com.cherrytwins.shop.catalog.web.dto;

import java.util.List;

public class ProductDetailResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private boolean active;
    private int basePriceCents;
    private String currency;

    private ArtistResponse artist;
    private CategoryResponse category;

    private List<ProductImageResponse> images;
    private List<ProductVariantResponse> variants;

    public ProductDetailResponse(Long id, String name, String slug, String description, boolean active,
                                 int basePriceCents, String currency, ArtistResponse artist, CategoryResponse category,
                                 List<ProductImageResponse> images, List<ProductVariantResponse> variants) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.active = active;
        this.basePriceCents = basePriceCents;
        this.currency = currency;
        this.artist = artist;
        this.category = category;
        this.images = images;
        this.variants = variants;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public int getBasePriceCents() { return basePriceCents; }
    public String getCurrency() { return currency; }
    public ArtistResponse getArtist() { return artist; }
    public CategoryResponse getCategory() { return category; }
    public List<ProductImageResponse> getImages() { return images; }
    public List<ProductVariantResponse> getVariants() { return variants; }
}