package com.cherrytwins.shop.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class AdminProductVariantRequest {

    @NotBlank @Size(max = 80)
    private String sku;

    @Size(max = 200)
    private String variantName;

    private Map<String, Object> attributes;

    @NotNull
    private Integer priceCents;

    @NotBlank @Size(min = 3, max = 3)
    private String currency;

    @NotNull
    private Integer stockOnHand;

    private Boolean active;

    public String getSku() { return sku; }
    public String getVariantName() { return variantName; }
    public Map<String, Object> getAttributes() { return attributes; }
    public Integer getPriceCents() { return priceCents; }
    public String getCurrency() { return currency; }
    public Integer getStockOnHand() { return stockOnHand; }
    public Boolean getActive() { return active; }

    public void setSku(String sku) { this.sku = sku; }
    public void setVariantName(String variantName) { this.variantName = variantName; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStockOnHand(Integer stockOnHand) { this.stockOnHand = stockOnHand; }
    public void setActive(Boolean active) { this.active = active; }
}