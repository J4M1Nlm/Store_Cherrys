package com.cherrytwins.shop.security.dto;

import lombok.Getter;

@Getter
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";

    public AuthResponse(String token){
        this.token = token;
    }
}
