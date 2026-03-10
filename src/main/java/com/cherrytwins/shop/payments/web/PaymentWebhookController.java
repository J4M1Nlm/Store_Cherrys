package com.cherrytwins.shop.payments.web;

import com.cherrytwins.shop.payments.web.dto.StripeWebhookRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments (Webhooks)", description = "Webhooks de proveedores de pago (Stripe real en el siguiente hito)")
@RestController
@RequestMapping("/api/payments/webhook")
public class PaymentWebhookController {

    @Operation(summary = "Webhook Stripe", description = "Placeholder: más adelante aquí validamos firma y actualizamos pagos/ordenes.")
    @PostMapping("/stripe")
    public ResponseEntity<Void> stripe(@RequestBody StripeWebhookRequest req) {
        // Por ahora solo respondemos 200. En Stripe real: verificar firma y procesar eventos.
        return ResponseEntity.ok().build();
    }
}