package com.cherrytwins.shop.catalog.web.dto;

public class ProductListItemResponse {
    private Long id;
    private String name;
    private String slug;
    private boolean active;
    private int basePriceCents;
    private String currency;
    private String mainImageUrl;

    private String artistSlug;
    private String categorySlug;

    public ProductListItemResponse(Long id, String name, String slug, boolean active,
                                   int basePriceCents, String currency, String mainImageUrl,
                                   String artistSlug, String categorySlug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.active = active;
        this.basePriceCents = basePriceCents;
        this.currency = currency;
        this.mainImageUrl = mainImageUrl;
        this.artistSlug = artistSlug;
        this.categorySlug = categorySlug;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public boolean isActive() { return active; }
    public int getBasePriceCents() { return basePriceCents; }
    public String getCurrency() { return currency; }
    public String getMainImageUrl() { return mainImageUrl; }
    public String getArtistSlug() { return artistSlug; }
    public String getCategorySlug() { return categorySlug; }
}