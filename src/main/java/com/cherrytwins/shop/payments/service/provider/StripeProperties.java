package com.cherrytwins.shop.payments.service.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.payments.stripe")
public class StripeProperties {
    private String secretKey;
    private String webhookSecret;

    public String getSecretKey() { return secretKey; }
    public String getWebhookSecret() { return webhookSecret; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    public void setWebhookSecret(String webhookSecret) { this.webhookSecret = webhookSecret; }
}