package com.cherrytwins.shop.coupons.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "coupon_redemptions")
public class CouponRedemption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "redeemed_at", nullable = false)
    private OffsetDateTime redeemedAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public Long getCouponId() { return couponId; }
    public Long getUserId() { return userId; }
    public Long getOrderId() { return orderId; }
    public OffsetDateTime getRedeemedAt() { return redeemedAt; }

    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}