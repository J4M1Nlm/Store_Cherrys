package com.cherrytwins.shop.cart.web.dto;

import java.util.Map;

public class CartItemResponse {
    private Long id;
    private Long variantId;
    private Long productId;

    private String productName;
    private String productSlug;

    private String sku;
    private String variantName;
    private Map<String, Object> attributes;

    private int quantity;
    private int unitPriceCents;
    private int lineTotalCents;
    private String currency;

    private int stockOnHand;
    private boolean variantActive;

    private String mainImageUrl;

    public CartItemResponse(Long id, Long variantId, Long productId,
                            String productName, String productSlug,
                            String sku, String variantName, Map<String, Object> attributes,
                            int quantity, int unitPriceCents, int lineTotalCents, String currency,
                            int stockOnHand, boolean variantActive, String mainImageUrl) {
        this.id = id;
        this.variantId = variantId;
        this.productId = productId;
        this.productName = productName;
        this.productSlug = productSlug;
        this.sku = sku;
        this.variantName = variantName;
        this.attributes = attributes;
        this.quantity = quantity;
        this.unitPriceCents = unitPriceCents;
        this.lineTotalCents = lineTotalCents;
        this.currency = currency;
        this.stockOnHand = stockOnHand;
        this.variantActive = variantActive;
        this.mainImageUrl = mainImageUrl;
    }

    public Long getId() { return id; }
    public Long getVariantId() { return variantId; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductSlug() { return productSlug; }
    public String getSku() { return sku; }
    public String getVariantName() { return variantName; }
    public Map<String, Object> getAttributes() { return attributes; }
    public int getQuantity() { return quantity; }
    public int getUnitPriceCents() { return unitPriceCents; }
    public int getLineTotalCents() { return lineTotalCents; }
    public String getCurrency() { return currency; }
    public int getStockOnHand() { return stockOnHand; }
    public boolean isVariantActive() { return variantActive; }
    public String getMainImageUrl() { return mainImageUrl; }
}