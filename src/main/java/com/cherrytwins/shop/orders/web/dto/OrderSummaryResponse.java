package com.cherrytwins.shop.orders.web.dto;

import java.time.OffsetDateTime;

public class OrderSummaryResponse {
    private Long id;
    private String status;
    private int totalCents;
    private String currency;
    private OffsetDateTime createdAt;

    public OrderSummaryResponse(Long id, String status, int totalCents, String currency, OffsetDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.totalCents = totalCents;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public int getTotalCents() { return totalCents; }
    public String getCurrency() { return currency; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}