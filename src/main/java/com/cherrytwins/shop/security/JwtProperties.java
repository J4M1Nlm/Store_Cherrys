package com.cherrytwins.shop.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private long expMinutes = 60;

    public String getSecret() { return secret; }
    public long getExpMinutes() { return expMinutes; }

    public void setSecret(String secret) { this.secret = secret; }
    public void setExpMinutes(long expMinutes) { this.expMinutes = expMinutes; }
}