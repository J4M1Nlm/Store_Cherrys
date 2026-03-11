package com.cherrytwins.shop.coupons.web.dto;

public class CouponValidateResponse {
    private boolean valid;
    private Long couponId;
    private int discountCents;
    private String message;

    public CouponValidateResponse(boolean valid, Long couponId, int discountCents, String message) {
        this.valid = valid;
        this.couponId = couponId;
        this.discountCents = discountCents;
        this.message = message;
    }

    public boolean isValid() { return valid; }
    public Long getCouponId() { return couponId; }
    public int getDiscountCents() { return discountCents; }
    public String getMessage() { return message; }
}