package com.cherrytwins.shop.payments.web.dto;

public class PaymentInitResponse {
    private Long paymentId;
    private String provider;
    private String clientSecret;
    private int amountCents;
    private String currency;

    public PaymentInitResponse(Long paymentId, String provider, String clientSecret, int amountCents, String currency) {
        this.paymentId = paymentId;
        this.provider = provider;
        this.clientSecret = clientSecret;
        this.amountCents = amountCents;
        this.currency = currency;
    }

    public Long getPaymentId() { return paymentId; }
    public String getProvider() { return provider; }
    public String getClientSecret() { return clientSecret; }
    public int getAmountCents() { return amountCents; }
    public String getCurrency() { return currency; }
}