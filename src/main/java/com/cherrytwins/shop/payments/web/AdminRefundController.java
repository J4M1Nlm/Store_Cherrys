package com.cherrytwins.shop.payments.web;

import com.cherrytwins.shop.payments.service.AdminRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments (Admin)", description = "Operaciones admin sobre pagos (refunds)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/payments")
public class AdminRefundController {

    private final AdminRefundService adminRefundService;

    public AdminRefundController(AdminRefundService adminRefundService) {
        this.adminRefundService = adminRefundService;
    }

    @Operation(summary = "Refund de orden", description = "Hace refund en Stripe (PaymentIntent) y marca Order/Payout como REFUNDED. Restock opcional.")
    @PostMapping("/refund")
    public ResponseEntity<Void> refund(@RequestParam Long orderId,
                                       @RequestParam(defaultValue = "false") boolean restock) {
        adminRefundService.refundOrder(orderId, restock);
        return ResponseEntity.noContent().build();
    }
}