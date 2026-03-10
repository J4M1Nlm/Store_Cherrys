package com.cherrytwins.shop.payments.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false, length = 40)
    private String provider; // STRIPE_SIM / STRIPE / PAYPAL...

    @Column(name = "provider_ref", length = 120)
    private String providerRef; // clientSecret simulado / payment_intent_id real

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.INITIATED;

    @Column(name = "amount_cents", nullable = false)
    private int amountCents;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void preUpdate() { this.updatedAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getProvider() { return provider; }
    public String getProviderRef() { return providerRef; }
    public PaymentStatus getStatus() { return status; }
    public int getAmountCents() { return amountCents; }
    public String getCurrency() { return currency; }
    public OffsetDateTime getPaidAt() { return paidAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setProvider(String provider) { this.provider = provider; }
    public void setProviderRef(String providerRef) { this.providerRef = providerRef; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setAmountCents(int amountCents) { this.amountCents = amountCents; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setPaidAt(OffsetDateTime paidAt) { this.paidAt = paidAt; }
}
