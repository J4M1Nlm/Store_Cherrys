package com.cherrytwins.shop.inventory.web.dto;

import java.time.OffsetDateTime;

public class InventoryMovementResponse {

    private Long id;
    private Long variantId;
    private String movementType;
    private int quantity; // signed (puede ser + o -)
    private String reason;
    private String referenceId;
    private OffsetDateTime createdAt;

    public InventoryMovementResponse(Long id, Long variantId, String movementType, int quantity,
                                     String reason, String referenceId, OffsetDateTime createdAt) {
        this.id = id;
        this.variantId = variantId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.reason = reason;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getVariantId() { return variantId; }
    public String getMovementType() { return movementType; }
    public int getQuantity() { return quantity; }
    public String getReason() { return reason; }
    public String getReferenceId() { return referenceId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

}