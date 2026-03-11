package com.cherrytwins.shop.coupons.web.dto;

import jakarta.validation.constraints.*;

public class CouponValidateRequest {

    @NotBlank
    @Size(max = 40)
    private String code;

    @NotNull
    @Min(0)
    private Integer subtotalCents;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    public String getCode() { return code; }
    public Integer getSubtotalCents() { return subtotalCents; }
    public String getCurrency() { return currency; }

    public void setCode(String code) { this.code = code; }
    public void setSubtotalCents(Integer subtotalCents) { this.subtotalCents = subtotalCents; }
    public void setCurrency(String currency) { this.currency = currency; }
}