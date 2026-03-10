package com.cherrytwins.shop.orders.web.dto;

import jakarta.validation.constraints.Size;

public class CheckoutRequest {

    private Long shippingAddressId;
    private Long billingAddressId;

    @Size(max = 40)
    private String couponCode;

    @Size(max = 2000)
    private String notes;

    public Long getShippingAddressId() { return shippingAddressId; }
    public Long getBillingAddressId() { return billingAddressId; }
    public String getCouponCode() { return couponCode; }
    public String getNotes() { return notes; }

    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }
    public void setBillingAddressId(Long billingAddressId) { this.billingAddressId = billingAddressId; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public void setNotes(String notes) { this.notes = notes; }
}
