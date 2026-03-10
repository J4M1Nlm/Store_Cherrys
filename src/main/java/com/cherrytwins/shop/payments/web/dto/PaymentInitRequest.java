package com.cherrytwins.shop.payments.web.dto;

import jakarta.validation.constraints.NotNull;

public class PaymentInitRequest {

    @NotNull
    private Long orderId;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}