package com.cherrytwins.shop.cart.web.dto;

import java.util.List;

public class CartResponse {
    private Long cartId;
    private String status;
    private String currency;
    private int itemsCount;
    private int subtotalCents;
    private List<CartItemResponse> items;

    public CartResponse(Long cartId, String status, String currency, int itemsCount, int subtotalCents, List<CartItemResponse> items) {
        this.cartId = cartId;
        this.status = status;
        this.currency = currency;
        this.itemsCount = itemsCount;
        this.subtotalCents = subtotalCents;
        this.items = items;
    }

    public Long getCartId() { return cartId; }
    public String getStatus() { return status; }
    public String getCurrency() { return currency; }
    public int getItemsCount() { return itemsCount; }
    public int getSubtotalCents() { return subtotalCents; }
    public List<CartItemResponse> getItems() { return items; }
}