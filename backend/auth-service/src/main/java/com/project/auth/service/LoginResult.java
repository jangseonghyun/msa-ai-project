package com.project.auth.service;

public class LoginResult {
    private final String accessToken;
    private final String refreshToken;

    public LoginResult(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() { return refreshToken; }
}
