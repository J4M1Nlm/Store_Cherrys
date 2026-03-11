package com.cherrytwins.shop.inventory.web.dto;

import java.util.Map;

public class InventoryVariantStockResponse {

    private Long variantId;
    private String sku;
    private String variantName;
    private Map<String, Object> attributes;
    private int stockOnHand;
    private boolean active;

    public InventoryVariantStockResponse(Long variantId, String sku, String variantName, Map<String, Object> attributes,
                                         int stockOnHand, boolean active) {
        this.variantId = variantId;
        this.sku = sku;
        this.variantName = variantName;
        this.attributes = attributes;
        this.stockOnHand = stockOnHand;
        this.active = active;
    }

    public Long getVariantId() { return variantId; }
    public String getSku() { return sku; }
    public String getVariantName() { return variantName; }
    public Map<String, Object> getAttributes() { return attributes; }
    public int getStockOnHand() { return stockOnHand; }
    public boolean isActive() { return active; }
}