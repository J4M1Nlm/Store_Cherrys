package com.cherrytwins.shop.orders.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variant_snapshot", nullable = false, columnDefinition = "jsonb")
    private JsonNode variantSnapshot;

    @Column(length = 80)
    private String sku;

    @Column(name = "unit_price_cents", nullable = false)
    private int unitPriceCents;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "line_total_cents", nullable = false)
    private int lineTotalCents;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getVariantId() { return variantId; }
    public String getProductName() { return productName; }
    public JsonNode getVariantSnapshot() { return variantSnapshot; }
    public String getSku() { return sku; }
    public int getUnitPriceCents() { return unitPriceCents; }
    public int getQuantity() { return quantity; }
    public int getLineTotalCents() { return lineTotalCents; }

    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setVariantSnapshot(JsonNode variantSnapshot) { this.variantSnapshot = variantSnapshot; }
    public void setSku(String sku) { this.sku = sku; }
    public void setUnitPriceCents(int unitPriceCents) { this.unitPriceCents = unitPriceCents; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setLineTotalCents(int lineTotalCents) { this.lineTotalCents = lineTotalCents; }
}
