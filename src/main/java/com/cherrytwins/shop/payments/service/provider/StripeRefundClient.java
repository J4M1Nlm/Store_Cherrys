package com.cherrytwins.shop.payments.service.provider;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import org.springframework.stereotype.Component;

@Component
public class StripeRefundClient {

    public Refund refundPaymentIntent(String paymentIntentId) {
        try {
            // Refund por PaymentIntent (Stripe permite refund usando payment_intent)
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();

            return Refund.create(params);
        } catch (StripeException e) {
            throw new BadRequestException("Stripe refund error: " + e.getMessage());
        }
    }
}