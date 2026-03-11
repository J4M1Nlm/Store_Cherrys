package com.cherrytwins.shop.payments.service.provider;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StripeClient {

    public PaymentIntent createPaymentIntent(long amountCents, String currency, Map<String, String> metadata) {
        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountCents)
                            .setCurrency(currency.toLowerCase())
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .putAllMetadata(metadata)
                            .build();

            return PaymentIntent.create(params);
        } catch (StripeException e) {
            throw new BadRequestException("Stripe error creating PaymentIntent: " + e.getMessage());
        }
    }
}