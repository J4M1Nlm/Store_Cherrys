package com.cherrytwins.shop.coupons.web.dto;

import java.time.OffsetDateTime;

public class CouponResponse {

    private Long id;
    private String code;
    private String discountType;
    private int discountValue;
    private String currency;

    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;

    private Integer maxRedemptions;
    private Integer perUserLimit;
    private int minOrderCents;

    private boolean active;
    private OffsetDateTime createdAt;

    public CouponResponse(Long id, String code, String discountType, int discountValue, String currency,
                          OffsetDateTime startsAt, OffsetDateTime endsAt,
                          Integer maxRedemptions, Integer perUserLimit, int minOrderCents,
                          boolean active, OffsetDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.currency = currency;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.maxRedemptions = maxRedemptions;
        this.perUserLimit = perUserLimit;
        this.minOrderCents = minOrderCents;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getDiscountType() { return discountType; }
    public int getDiscountValue() { return discountValue; }
    public String getCurrency() { return currency; }
    public OffsetDateTime getStartsAt() { return startsAt; }
    public OffsetDateTime getEndsAt() { return endsAt; }
    public Integer getMaxRedemptions() { return maxRedemptions; }
    public Integer getPerUserLimit() { return perUserLimit; }
    public int getMinOrderCents() { return minOrderCents; }
    public boolean isActive() { return active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}