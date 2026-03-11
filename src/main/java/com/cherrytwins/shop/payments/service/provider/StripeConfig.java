package com.cherrytwins.shop.payments.service.provider;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StripeProperties.class)
public class StripeConfig {

    private final StripeProperties props;

    public StripeConfig(StripeProperties props) { this.props = props; }

    @PostConstruct
    public void init() {
        // Inicializa Stripe API key globalmente
        if (props.getSecretKey() != null && !props.getSecretKey().isBlank()) {
            Stripe.apiKey = props.getSecretKey();
        }
    }
}