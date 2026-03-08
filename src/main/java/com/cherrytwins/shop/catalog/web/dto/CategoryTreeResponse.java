package com.cherrytwins.shop.catalog.web.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryTreeResponse {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;
    private List<CategoryTreeResponse> children = new ArrayList<>();

    public CategoryTreeResponse(Long id, String name, String slug, Long parentId) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public Long getParentId() { return parentId; }
    public List<CategoryTreeResponse> getChildren() { return children; }

    public void addChild(CategoryTreeResponse child) { this.children.add(child); }
}
