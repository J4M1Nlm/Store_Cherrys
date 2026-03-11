package com.cherrytwins.shop.coupons.web;

import com.cherrytwins.shop.coupons.service.CouponService;
import com.cherrytwins.shop.coupons.web.dto.CouponValidateRequest;
import com.cherrytwins.shop.coupons.web.dto.CouponValidateResponse;
import com.cherrytwins.shop.security.CurrentUser;
import com.cherrytwins.shop.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coupons", description = "Validación de cupones para customers")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "Validar cupón", description = "Valida reglas de cupón y devuelve el descuento calculado (en cents).")
    @PostMapping("/validate")
    public CouponValidateResponse validate(@CurrentUser UserPrincipal principal,
                                           @Valid @RequestBody CouponValidateRequest req) {
        try {
            var app = couponService.validateAndCompute(
                    req.getCode(),
                    principal.getId(),
                    req.getSubtotalCents(),
                    req.getCurrency()
            );
            return new CouponValidateResponse(true, app.couponId(), app.discountCents(), "OK");
        } catch (Exception ex) {
            return new CouponValidateResponse(false, null, 0, ex.getMessage());
        }
    }
}