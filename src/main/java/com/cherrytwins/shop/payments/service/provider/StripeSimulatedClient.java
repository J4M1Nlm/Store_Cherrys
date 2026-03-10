package com.cherrytwins.shop.payments.service.provider;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class StripeSimulatedClient {

    private final SecureRandom random = new SecureRandom();

    public String createClientSecret() {
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return "cs_test_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}