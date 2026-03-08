package com.cherrytwins.shop.catalog.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, unique = true, length = 80)
    private String sku;

    @Column(name = "variant_name", length = 200)
    private String variantName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> attributes = new HashMap<>();

    @Column(name = "price_cents", nullable = false)
    private int priceCents;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "stock_on_hand", nullable = false)
    private int stockOnHand = 0;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void preUpdate() { this.updatedAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getSku() { return sku; }
    public String getVariantName() { return variantName; }
    public Map<String, Object> getAttributes() { return attributes; }
    public int getPriceCents() { return priceCents; }
    public String getCurrency() { return currency; }
    public int getStockOnHand() { return stockOnHand; }
    public boolean isActive() { return active; }

    public void setId(Long id) { this.id = id; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setSku(String sku) { this.sku = sku; }
    public void setVariantName(String variantName) { this.variantName = variantName; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes == null ? new HashMap<>() : attributes; }
    public void setPriceCents(int priceCents) { this.priceCents = priceCents; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStockOnHand(int stockOnHand) { this.stockOnHand = stockOnHand; }
    public void setActive(boolean active) { this.active = active; }
}