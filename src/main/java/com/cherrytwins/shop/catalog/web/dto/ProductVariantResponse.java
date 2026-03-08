package com.cherrytwins.shop.catalog.web.dto;

import java.util.Map;

public class ProductVariantResponse {
    private Long id;
    private String sku;
    private String variantName;
    private Map<String, Object> attributes;
    private int priceCents;
    private String currency;
    private int stockOnHand;
    private boolean active;

    public ProductVariantResponse(Long id, String sku, String variantName, Map<String, Object> attributes,
                                  int priceCents, String currency, int stockOnHand, boolean active) {
        this.id = id;
        this.sku = sku;
        this.variantName = variantName;
        this.attributes = attributes;
        this.priceCents = priceCents;
        this.currency = currency;
        this.stockOnHand = stockOnHand;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getVariantName() { return variantName; }
    public Map<String, Object> getAttributes() { return attributes; }
    public int getPriceCents() { return priceCents; }
    public String getCurrency() { return currency; }
    public int getStockOnHand() { return stockOnHand; }
    public boolean isActive() { return active; }
}
