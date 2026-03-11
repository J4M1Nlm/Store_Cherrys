package com.cherrytwins.shop.coupons.service;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.coupons.domain.Coupon;
import com.cherrytwins.shop.coupons.domain.DiscountType;
import com.cherrytwins.shop.coupons.repository.CouponRepository;
import com.cherrytwins.shop.coupons.web.dto.AdminCouponRequest;
import com.cherrytwins.shop.coupons.web.dto.CouponResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AdminCouponService {

    private final CouponRepository couponRepository;

    public AdminCouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Page<CouponResponse> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return couponRepository.findAll(pageable).map(this::toResponse);
    }

    public CouponResponse get(Long id) {
        return toResponse(couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coupon not found")));
    }

    @Transactional
    public CouponResponse create(AdminCouponRequest req) {
        String code = normalizeCode(req.getCode());
        couponRepository.findByCodeIgnoreCase(code).ifPresent(c -> {
            throw new BadRequestException("Coupon code already exists");
        });

        Coupon c = new Coupon();
        c.setCode(code);
        apply(req, c, true);

        c = couponRepository.save(c);
        return toResponse(c);
    }

    @Transactional
    public CouponResponse update(Long id, AdminCouponRequest req) {
        Coupon c = couponRepository.findById(id).orElseThrow(() -> new NotFoundException("Coupon not found"));

        String newCode = normalizeCode(req.getCode());
        if (!newCode.equalsIgnoreCase(c.getCode())) {
            couponRepository.findByCodeIgnoreCase(newCode).ifPresent(existing -> {
                throw new BadRequestException("Coupon code already exists");
            });
            c.setCode(newCode);
        }

        apply(req, c, false);
        return toResponse(c);
    }

    @Transactional
    public CouponResponse setActive(Long id, boolean value) {
        Coupon c = couponRepository.findById(id).orElseThrow(() -> new NotFoundException("Coupon not found"));
        c.setActive(value);
        return toResponse(c);
    }

    @Transactional
    public void delete(Long id) {
        if (!couponRepository.existsById(id)) throw new NotFoundException("Coupon not found");
        couponRepository.deleteById(id);
    }

    // ---------------- Helpers ----------------

    private void apply(AdminCouponRequest req, Coupon c, boolean creating) {
        DiscountType dt = parseDiscountType(req.getDiscountType());

        if (req.getStartsAt() != null && req.getEndsAt() != null && req.getStartsAt().isAfter(req.getEndsAt())) {
            throw new BadRequestException("startsAt must be <= endsAt");
        }

        int dv = req.getDiscountValue();
        if (dt == DiscountType.PERCENT && (dv < 1 || dv > 100)) {
            throw new BadRequestException("PERCENT discountValue must be between 1 and 100");
        }
        if (dt == DiscountType.FIXED && dv <= 0) {
            throw new BadRequestException("FIXED discountValue must be > 0 (cents)");
        }

        c.setDiscountType(dt);
        c.setDiscountValue(dv);
        c.setCurrency(req.getCurrency().trim().toUpperCase(Locale.ROOT));
        c.setStartsAt(req.getStartsAt());
        c.setEndsAt(req.getEndsAt());
        c.setMaxRedemptions(req.getMaxRedemptions());
        c.setPerUserLimit(req.getPerUserLimit());
        c.setMinOrderCents(req.getMinOrderCents());

        if (req.getActive() != null) c.setActive(req.getActive());
        else if (creating) c.setActive(true);
    }

    private DiscountType parseDiscountType(String s) {
        if (s == null) throw new BadRequestException("discountType is required");
        try {
            return DiscountType.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new BadRequestException("Invalid discountType. Use PERCENT or FIXED");
        }
    }

    private String normalizeCode(String code) {
        if (code == null) throw new BadRequestException("code is required");
        String c = code.trim().toUpperCase(Locale.ROOT);
        if (c.isEmpty()) throw new BadRequestException("code is required");
        return c;
    }

    private CouponResponse toResponse(Coupon c) {
        return new CouponResponse(
                c.getId(),
                c.getCode(),
                c.getDiscountType().name(),
                c.getDiscountValue(),
                c.getCurrency(),
                c.getStartsAt(),
                c.getEndsAt(),
                c.getMaxRedemptions(),
                c.getPerUserLimit(),
                c.getMinOrderCents(),
                c.isActive(),
                c.getCreatedAt()
        );
    }
}