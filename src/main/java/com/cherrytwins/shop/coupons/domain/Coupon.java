package com.cherrytwins.shop.coupons.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private int discountValue;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "starts_at")
    private OffsetDateTime startsAt;

    @Column(name = "ends_at")
    private OffsetDateTime endsAt;

    @Column(name = "max_redemptions")
    private Integer maxRedemptions;

    @Column(name = "per_user_limit")
    private Integer perUserLimit;

    @Column(name = "min_order_cents", nullable = false)
    private int minOrderCents;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void preUpdate() { this.updatedAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public DiscountType getDiscountType() { return discountType; }
    public int getDiscountValue() { return discountValue; }
    public String getCurrency() { return currency; }
    public OffsetDateTime getStartsAt() { return startsAt; }
    public OffsetDateTime getEndsAt() { return endsAt; }
    public Integer getMaxRedemptions() { return maxRedemptions; }
    public Integer getPerUserLimit() { return perUserLimit; }
    public int getMinOrderCents() { return minOrderCents; }
    public boolean isActive() { return active; }

    public void setCode(String code) { this.code = code; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }
    public void setDiscountValue(int discountValue) { this.discountValue = discountValue; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStartsAt(OffsetDateTime startsAt) { this.startsAt = startsAt; }
    public void setEndsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; }
    public void setMaxRedemptions(Integer maxRedemptions) { this.maxRedemptions = maxRedemptions; }
    public void setPerUserLimit(Integer perUserLimit) { this.perUserLimit = perUserLimit; }
    public void setMinOrderCents(int minOrderCents) { this.minOrderCents = minOrderCents; }
    public void setActive(boolean active) { this.active = active; }
}