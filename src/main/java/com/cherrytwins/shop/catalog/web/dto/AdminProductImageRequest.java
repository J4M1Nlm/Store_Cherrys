package com.cherrytwins.shop.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminProductImageRequest {
    @NotBlank
    private String url;

    @Size(max = 255)
    private String altText;

    @NotNull
    private Integer sortOrder;

    public String getUrl() { return url; }
    public String getAltText() { return altText; }
    public Integer getSortOrder() { return sortOrder; }

    public void setUrl(String url) { this.url = url; }
    public void setAltText(String altText) { this.altText = altText; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
