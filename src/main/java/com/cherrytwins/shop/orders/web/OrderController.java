package com.cherrytwins.shop.orders.web;

import com.cherrytwins.shop.common.pagination.PageResponse;
import com.cherrytwins.shop.orders.service.CheckoutService;
import com.cherrytwins.shop.orders.service.OrderQueryService;
import com.cherrytwins.shop.orders.web.dto.CheckoutRequest;
import com.cherrytwins.shop.orders.web.dto.OrderDetailResponse;
import com.cherrytwins.shop.orders.web.dto.OrderSummaryResponse;
import com.cherrytwins.shop.security.CurrentUser;
import com.cherrytwins.shop.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders", description = "Pedidos del usuario autenticado + checkout")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CheckoutService checkoutService;
    private final OrderQueryService orderQueryService;

    public OrderController(CheckoutService checkoutService, OrderQueryService orderQueryService) {
        this.checkoutService = checkoutService;
        this.orderQueryService = orderQueryService;
    }

    @Operation(summary = "Checkout", description = "Convierte el carrito ACTIVE en una orden PENDING, reserva stock y marca el carrito como CHECKED_OUT.")
    @PostMapping("/checkout")
    public OrderDetailResponse checkout(@CurrentUser UserPrincipal principal,
                                        @Valid @RequestBody CheckoutRequest req) {
        return checkoutService.checkout(principal.getId(), req);
    }

    @Operation(summary = "Mis pedidos (paginado)")
    @GetMapping
    public PageResponse<OrderSummaryResponse> myOrders(@CurrentUser UserPrincipal principal,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return orderQueryService.myOrders(principal.getId(), page, size);
    }

    @Operation(summary = "Detalle de mi pedido")
    @GetMapping("/{orderId}")
    public OrderDetailResponse myOrder(@CurrentUser UserPrincipal principal, @PathVariable Long orderId) {
        return orderQueryService.myOrderDetail(principal.getId(), orderId);
    }
}