package com.cherrytwins.shop.cart.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddCartItemRequest {
    @NotNull
    private Long variantId;

    @Min(1)
    private Integer quantity;

    public Long getVariantId() { return variantId; }
    public Integer getQuantity() { return quantity; }

    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}