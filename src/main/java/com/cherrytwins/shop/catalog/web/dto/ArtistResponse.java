package com.cherrytwins.shop.catalog.web.dto;

public class ArtistResponse {
    private Long id;
    private String name;
    private String slug;
    private String bio;

    public ArtistResponse(Long id, String name, String slug, String bio) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.bio = bio;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getBio() { return bio; }
}