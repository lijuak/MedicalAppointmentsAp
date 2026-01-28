package com.medicalapp.dto;

import lombok.Data;

public class TokenLoginRequest {
    private String token;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
