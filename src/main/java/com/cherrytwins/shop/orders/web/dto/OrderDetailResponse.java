package com.cherrytwins.shop.orders.web.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class OrderDetailResponse {
    private Long id;
    private Long userId;
    private String status;
    private String currency;

    private int subtotalCents;
    private int discountCents;
    private int shippingCents;
    private int taxCents;
    private int totalCents;

    private Long couponId;
    private Long shippingAddressId;
    private Long billingAddressId;

    private OffsetDateTime placedAt;
    private OffsetDateTime createdAt;

    private List<OrderItemResponse> items;

    public OrderDetailResponse(Long id, Long userId, String status, String currency,
                               int subtotalCents, int discountCents, int shippingCents, int taxCents, int totalCents,
                               Long couponId, Long shippingAddressId, Long billingAddressId,
                               OffsetDateTime placedAt, OffsetDateTime createdAt,
                               List<OrderItemResponse> items) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.currency = currency;
        this.subtotalCents = subtotalCents;
        this.discountCents = discountCents;
        this.shippingCents = shippingCents;
        this.taxCents = taxCents;
        this.totalCents = totalCents;
        this.couponId = couponId;
        this.shippingAddressId = shippingAddressId;
        this.billingAddressId = billingAddressId;
        this.placedAt = placedAt;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getStatus() { return status; }
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
    public List<OrderItemResponse> getItems() { return items; }
}