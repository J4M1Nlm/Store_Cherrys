package com.cherrytwins.shop.payments.web.dto;

public class StripeWebhookRequest {
    private String type;   // e.g. "payment_intent.succeeded"
    private Object data;   // lo dejamos genérico por ahora

    public String getType() { return type; }
    public Object getData() { return data; }

    public void setType(String type) { this.type = type; }
    public void setData(Object data) { this.data = data; }
}
