package com.cherrytwins.shop.orders.web;

import com.cherrytwins.shop.common.pagination.PageResponse;
import com.cherrytwins.shop.orders.domain.OrderStatus;
import com.cherrytwins.shop.orders.service.AdminOrderService;
import com.cherrytwins.shop.orders.service.OrderQueryService;
import com.cherrytwins.shop.orders.web.dto.OrderSummaryResponse;
import com.cherrytwins.shop.orders.web.dto.UpdateOrderStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders (Admin)", description = "Gestión de pedidos para administradores")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderQueryService orderQueryService;
    private final AdminOrderService adminOrderService;

    public AdminOrderController(OrderQueryService orderQueryService, AdminOrderService adminOrderService) {
        this.orderQueryService = orderQueryService;
        this.adminOrderService = adminOrderService;
    }

    @Operation(summary = "Listar pedidos (paginado)", description = "Filtro opcional por status.")
    @GetMapping
    public PageResponse<OrderSummaryResponse> list(@RequestParam(required = false) OrderStatus status,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        return orderQueryService.adminOrders(status, page, size);
    }

    @Operation(summary = "Cambiar status de una orden")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long orderId, @Valid @RequestBody UpdateOrderStatusRequest req) {
        adminOrderService.updateStatus(orderId, OrderStatus.valueOf(req.getStatus().trim().toUpperCase()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cancelar orden y restock", description = "Cancela la orden y regresa stock a variantes (crea movimientos IN).")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId) {
        adminOrderService.cancelAndRestock(orderId);
        return ResponseEntity.noContent().build();
    }
}