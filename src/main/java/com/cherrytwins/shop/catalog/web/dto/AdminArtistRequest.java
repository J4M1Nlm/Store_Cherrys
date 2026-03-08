package com.cherrytwins.shop.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminArtistRequest {
    @NotBlank @Size(max = 200)
    private String name;

    @Size(max = 220)
    private String slug;

    private String bio;

    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getBio() { return bio; }

    public void setName(String name) { this.name = name; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setBio(String bio) { this.bio = bio; }
}