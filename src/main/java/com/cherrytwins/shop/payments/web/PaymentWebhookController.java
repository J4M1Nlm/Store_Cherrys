package com.cherrytwins.shop.payments.web;

import com.cherrytwins.shop.payments.service.PaymentService;
import com.cherrytwins.shop.payments.service.provider.StripeProperties;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Tag(name = "Payments (Webhooks)", description = "Webhooks de Stripe (real)")
@RestController
@RequestMapping("/api/payments/webhook")
public class PaymentWebhookController {

    private final StripeProperties stripeProperties;
    private final PaymentService paymentService;

    public PaymentWebhookController(StripeProperties stripeProperties, PaymentService paymentService) {
        this.stripeProperties = stripeProperties;
        this.paymentService = paymentService;
    }

    @Operation(summary = "Webhook Stripe", description = "Valida firma y procesa eventos payment_intent.succeeded / payment_intent.payment_failed")
    @PostMapping("/stripe")
    public ResponseEntity<String> stripeWebhook(HttpServletRequest request,
                                                @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) throws Exception {

        String payload = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        if (sigHeader == null || sigHeader.isBlank()) {
            return ResponseEntity.badRequest().body("Missing Stripe-Signature header");
        }

        String secret = stripeProperties.getWebhookSecret();
        if (secret == null || secret.isBlank()) {
            return ResponseEntity.status(500).body("Webhook secret not configured");
        }

        final Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, secret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("Invalid signature");
        }

        // Procesar eventos clave
        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent pi = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

                if (pi != null) {
                    paymentService.markCapturedByProviderRef(pi.getId());
                }
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent pi = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

                if (pi != null) {
                    paymentService.markFailedByProviderRef(pi.getId());
                }
            }
            default -> {
                // Ignoramos otros por ahora
            }
        }

        return ResponseEntity.ok("ok");
    }
}