package com.cherrytwins.shop.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminCategoryRequest {
    @NotBlank @Size(max = 120)
    private String name;

    @Size(max = 140)
    private String slug;

    private Long parentId;

    public String getName() { return name; }
    public String getSlug() { return slug; }
    public Long getParentId() { return parentId; }

    public void setName(String name) { this.name = name; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}