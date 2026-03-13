package com.cherrytwins.shop.payments.web;

import com.cherrytwins.shop.payments.service.PaymentService;
import com.cherrytwins.shop.payments.service.provider.StripeProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
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
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentWebhookController(StripeProperties stripeProperties, PaymentService paymentService) {
        this.stripeProperties = stripeProperties;
        this.paymentService = paymentService;
    }

    @Operation(summary = "Webhook Stripe", description = "Valida firma y procesa payment_intent.succeeded / payment_intent.payment_failed")
    @PostMapping("/stripe")
    public ResponseEntity<String> stripeWebhook(HttpServletRequest request,
                                                @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader)
            throws Exception {

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

        // ✅ Siempre extraemos IDs desde payload raw (no dependemos del deserializer)
        JsonNode root = mapper.readTree(payload);
        JsonNode obj = root.path("data").path("object");

        String eventType = event.getType();

        // payment_intent id directo
        String paymentIntentId = null;
        if (eventType.startsWith("payment_intent.")) {
            paymentIntentId = textOrNull(obj.path("id"));
        }

        // si llega un charge.* (a veces también es útil), trae payment_intent dentro del charge
        if (paymentIntentId == null && eventType.startsWith("charge.")) {
            paymentIntentId = textOrNull(obj.path("payment_intent"));
        }

        switch (eventType) {
            case "payment_intent.succeeded" -> {
                if (paymentIntentId != null) paymentService.markCapturedByProviderRef(paymentIntentId);
            }
            case "payment_intent.payment_failed" -> {
                if (paymentIntentId != null) paymentService.markFailedByProviderRef(paymentIntentId);
            }
            default -> {
                // ignoramos otros eventos pero respondemos 200
            }
        }

        return ResponseEntity.ok("ok");
    }

    private String textOrNull(JsonNode node) {
        if (node == null) return null;
        String s = node.asText(null);
        return (s == null || s.isBlank()) ? null : s;
    }
}