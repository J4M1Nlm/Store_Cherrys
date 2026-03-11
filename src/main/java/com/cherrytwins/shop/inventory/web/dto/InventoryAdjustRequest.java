package com.cherrytwins.shop.inventory.web.dto;

import jakarta.validation.constraints.*;

public class InventoryAdjustRequest {

    @NotNull
    private Long variantId;

    @NotBlank
    @Size(max = 30)
    private String movementType; // IN, OUT, ADJUST

    @NotNull
    @Min(1)
    private Integer quantity; // siempre positivo en request (el servicio decide signo)

    @Size(max = 255)
    private String reason;

    @Size(max = 80)
    private String referenceId;

    public Long getVariantId() { return variantId; }
    public String getMovementType() { return movementType; }
    public Integer getQuantity() { return quantity; }
    public String getReason() { return reason; }
    public String getReferenceId() { return referenceId; }

    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setReason(String reason) { this.reason = reason; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
}