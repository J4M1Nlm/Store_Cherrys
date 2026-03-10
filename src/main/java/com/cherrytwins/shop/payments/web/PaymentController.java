package com.cherrytwins.shop.payments.web;

import com.cherrytwins.shop.payments.service.PaymentService;
import com.cherrytwins.shop.payments.web.dto.PaymentInitRequest;
import com.cherrytwins.shop.payments.web.dto.PaymentInitResponse;
import com.cherrytwins.shop.security.CurrentUser;
import com.cherrytwins.shop.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments", description = "Pagos del usuario (Stripe simulado por ahora)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

    @Operation(summary = "Inicializar pago", description = "Crea un Payment INITIATED para la orden (PENDING) y devuelve clientSecret simulado.")
    @PostMapping("/init")
    public PaymentInitResponse init(@CurrentUser UserPrincipal principal,
                                    @Valid @RequestBody PaymentInitRequest req) {
        return paymentService.initPayment(principal.getId(), req.getOrderId());
    }

    @Operation(summary = "Simular pago exitoso", description = "Cambia Payment -> CAPTURED y Order -> PAID.")
    @PostMapping("/{paymentId}/simulate/succeed")
    public ResponseEntity<Void> simulateSucceed(@CurrentUser UserPrincipal principal,
                                                @PathVariable Long paymentId) {
        paymentService.simulateSucceed(principal.getId(), paymentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Simular pago fallido", description = "Cambia Payment -> FAILED. No cambia Order.")
    @PostMapping("/{paymentId}/simulate/fail")
    public ResponseEntity<Void> simulateFail(@CurrentUser UserPrincipal principal,
                                             @PathVariable Long paymentId) {
        paymentService.simulateFail(principal.getId(), paymentId);
        return ResponseEntity.noContent().build();
    }
}