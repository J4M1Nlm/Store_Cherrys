package com.cherrytwins.shop.cart.web;

import com.cherrytwins.shop.cart.service.CartService;
import com.cherrytwins.shop.cart.web.dto.*;
import com.cherrytwins.shop.security.CurrentUser;
import com.cherrytwins.shop.security.UserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "Carrito del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) { this.cartService = cartService; }

    @Operation(summary = "Obtener (o crear) mi carrito activo")
    @GetMapping
    public CartResponse getMyCart(@CurrentUser UserPrincipal principal) {
        return cartService.getOrCreateMyCart(principal.getId());
    }

    @Operation(summary = "Agregar item al carrito", description = "Si ya existe el variant en el carrito, acumula quantity.")
    @PostMapping("/items")
    public CartResponse addItem(@CurrentUser UserPrincipal principal,
                                @Valid @RequestBody AddCartItemRequest req) {
        return cartService.addItem(principal.getId(), req);
    }

    @Operation(summary = "Actualizar cantidad de un item")
    @PutMapping("/items/{itemId}")
    public CartResponse updateQty(@CurrentUser UserPrincipal principal,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody UpdateCartItemQuantityRequest req) {
        return cartService.updateItemQuantity(principal.getId(), itemId, req);
    }

    @Operation(summary = "Eliminar item del carrito")
    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(@CurrentUser UserPrincipal principal,
                                   @PathVariable Long itemId) {
        return cartService.removeItem(principal.getId(), itemId);
    }

    @Operation(summary = "Vaciar carrito")
    @DeleteMapping
    public CartResponse clear(@CurrentUser UserPrincipal principal) {
        return cartService.clearCart(principal.getId());
    }

    @Operation(summary = "Abandonar carrito", description = "Cambia el status del carrito ACTIVE a ABANDONED. No borra items.")
    @PostMapping("/abandon")
    public ResponseEntity<Void> abandon(@CurrentUser UserPrincipal principal) {
        cartService.abandonCart(principal.getId());
        return ResponseEntity.noContent().build();
    }
}