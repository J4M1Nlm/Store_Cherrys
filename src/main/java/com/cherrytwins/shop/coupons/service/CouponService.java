package com.cherrytwins.shop.coupons.service;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.coupons.domain.Coupon;
import com.cherrytwins.shop.coupons.domain.CouponRedemption;
import com.cherrytwins.shop.coupons.domain.DiscountType;
import com.cherrytwins.shop.coupons.repository.CouponRedemptionRepository;
import com.cherrytwins.shop.coupons.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;

    public CouponService(CouponRepository couponRepository, CouponRedemptionRepository redemptionRepository) {
        this.couponRepository = couponRepository;
        this.redemptionRepository = redemptionRepository;
    }

    public record CouponApplication(Long couponId, int discountCents) {}

    public CouponApplication validateAndCompute(String couponCode, Long userId, int subtotalCents, String currency) {
        if (couponCode == null || couponCode.isBlank()) return new CouponApplication(null, 0);

        Coupon c = couponRepository.findByCodeIgnoreCase(couponCode.trim())
                .orElseThrow(() -> new BadRequestException("Invalid coupon"));

        if (!c.isActive()) throw new BadRequestException("Coupon is not active");
        if (!c.getCurrency().equalsIgnoreCase(currency)) throw new BadRequestException("Coupon currency mismatch");
        if (subtotalCents < c.getMinOrderCents()) throw new BadRequestException("Order does not meet coupon minimum");

        OffsetDateTime now = OffsetDateTime.now();
        if (c.getStartsAt() != null && now.isBefore(c.getStartsAt())) throw new BadRequestException("Coupon not started");
        if (c.getEndsAt() != null && now.isAfter(c.getEndsAt())) throw new BadRequestException("Coupon expired");

        if (c.getMaxRedemptions() != null) {
            long total = redemptionRepository.countByCouponId(c.getId());
            if (total >= c.getMaxRedemptions()) throw new BadRequestException("Coupon redemption limit reached");
        }

        if (c.getPerUserLimit() != null) {
            long perUser = redemptionRepository.countByCouponIdAndUserId(c.getId(), userId);
            if (perUser >= c.getPerUserLimit()) throw new BadRequestException("Coupon per-user limit reached");
        }

        int discount;
        if (c.getDiscountType() == DiscountType.PERCENT) {
            discount = (int) Math.floor(subtotalCents * (c.getDiscountValue() / 100.0));
        } else {
            discount = c.getDiscountValue();
        }

        if (discount < 0) discount = 0;
        if (discount > subtotalCents) discount = subtotalCents;

        return new CouponApplication(c.getId(), discount);
    }

    @Transactional
    public void redeem(Long couponId, Long userId, Long orderId) {
        if (couponId == null) return;
        CouponRedemption r = new CouponRedemption();
        r.setCouponId(couponId);
        r.setUserId(userId);
        r.setOrderId(orderId);
        redemptionRepository.save(r);
    }
}
