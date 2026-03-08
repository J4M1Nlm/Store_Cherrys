package com.cherrytwins.shop.catalog.web.dto;

public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;

    public CategoryResponse(Long id, String name, String slug, Long parentId) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public Long getParentId() { return parentId; }
}