package com.cherrytwins.shop.coupons.web.dto;

import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public class AdminCouponRequest {

    @NotBlank
    @Size(max = 40)
    private String code;

    @NotBlank
    @Size(max = 20)
    private String discountType; // PERCENT | FIXED

    @NotNull
    @Min(1)
    private Integer discountValue; // percent: 1..100, fixed: cents > 0

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;

    @Min(0)
    private Integer maxRedemptions;

    @Min(0)
    private Integer perUserLimit;

    @NotNull
    @Min(0)
    private Integer minOrderCents;

    private Boolean active;

    public String getCode() { return code; }
    public String getDiscountType() { return discountType; }
    public Integer getDiscountValue() { return discountValue; }
    public String getCurrency() { return currency; }
    public OffsetDateTime getStartsAt() { return startsAt; }
    public OffsetDateTime getEndsAt() { return endsAt; }
    public Integer getMaxRedemptions() { return maxRedemptions; }
    public Integer getPerUserLimit() { return perUserLimit; }
    public Integer getMinOrderCents() { return minOrderCents; }
    public Boolean getActive() { return active; }

    public void setCode(String code) { this.code = code; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public void setDiscountValue(Integer discountValue) { this.discountValue = discountValue; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStartsAt(OffsetDateTime startsAt) { this.startsAt = startsAt; }
    public void setEndsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; }
    public void setMaxRedemptions(Integer maxRedemptions) { this.maxRedemptions = maxRedemptions; }
    public void setPerUserLimit(Integer perUserLimit) { this.perUserLimit = perUserLimit; }
    public void setMinOrderCents(Integer minOrderCents) { this.minOrderCents = minOrderCents; }
    public void setActive(Boolean active) { this.active = active; }
}
