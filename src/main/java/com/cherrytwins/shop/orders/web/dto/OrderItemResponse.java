package com.cherrytwins.shop.orders.web.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class OrderItemResponse {
    private Long id;
    private Long variantId;
    private String productName;
    private String sku;
    private JsonNode variantSnapshot;
    private int unitPriceCents;
    private int quantity;
    private int lineTotalCents;

    public OrderItemResponse(Long id, Long variantId, String productName, String sku,
                             JsonNode variantSnapshot, int unitPriceCents, int quantity, int lineTotalCents) {
        this.id = id;
        this.variantId = variantId;
        this.productName = productName;
        this.sku = sku;
        this.variantSnapshot = variantSnapshot;
        this.unitPriceCents = unitPriceCents;
        this.quantity = quantity;
        this.lineTotalCents = lineTotalCents;
    }

    public Long getId() { return id; }
    public Long getVariantId() { return variantId; }
    public String getProductName() { return productName; }
    public String getSku() { return sku; }
    public JsonNode getVariantSnapshot() { return variantSnapshot; }
    public int getUnitPriceCents() { return unitPriceCents; }
    public int getQuantity() { return quantity; }
    public int getLineTotalCents() { return lineTotalCents; }
}