package com.cherrytwins.shop.orders.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "subtotal_cents", nullable = false)
    private int subtotalCents;

    @Column(name = "discount_cents", nullable = false)
    private int discountCents;

    @Column(name = "shipping_cents", nullable = false)
    private int shippingCents;

    @Column(name = "tax_cents", nullable = false)
    private int taxCents;

    @Column(name = "total_cents", nullable = false)
    private int totalCents;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "shipping_address_id")
    private Long shippingAddressId;

    @Column(name = "billing_address_id")
    private Long billingAddressId;

    @Column(name = "placed_at")
    private OffsetDateTime placedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void preUpdate() { this.updatedAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public OrderStatus getStatus() { return status; }
    public String getCurrency() { return currency; }
    public int getSubtotalCents() { return subtotalCents; }
    public int getDiscountCents() { return discountCents; }
    public int getShippingCents() { return shippingCents; }
    public int getTaxCents() { return taxCents; }
    public int getTotalCents() { return totalCents; }
    public Long getCouponId() { return couponId; }
    public Long getShippingAddressId() { return shippingAddressId; }
    public Long getBillingAddressId() { return billingAddressId; }
    public OffsetDateTime getPlacedAt() { return placedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setSubtotalCents(int subtotalCents) { this.subtotalCents = subtotalCents; }
    public void setDiscountCents(int discountCents) { this.discountCents = discountCents; }
    public void setShippingCents(int shippingCents) { this.shippingCents = shippingCents; }
    public void setTaxCents(int taxCents) { this.taxCents = taxCents; }
    public void setTotalCents(int totalCents) { this.totalCents = totalCents; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }
    public void setBillingAddressId(Long billingAddressId) { this.billingAddressId = billingAddressId; }
    public void setPlacedAt(OffsetDateTime placedAt) { this.placedAt = placedAt; }
}