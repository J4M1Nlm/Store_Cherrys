package com.cherrytwins.shop.inventory.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variant_id", nullable = false)
    private Long variantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementType movementType;

    @Column(nullable = false)
    private int quantity; // en tu schema puede ser + o - (chk <> 0)

    @Column(length = 255)
    private String reason;

    @Column(name = "reference_id", length = 80)
    private String referenceId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public Long getVariantId() { return variantId; }
    public MovementType getMovementType() { return movementType; }
    public int getQuantity() { return quantity; }
    public String getReason() { return reason; }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getReferenceId() { return referenceId; }

    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setReason(String reason) { this.reason = reason; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
}