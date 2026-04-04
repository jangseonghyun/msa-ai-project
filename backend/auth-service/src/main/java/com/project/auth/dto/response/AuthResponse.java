package com.project.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthResponse {

    @Getter
    @AllArgsConstructor
    public static class DuplicateIdResponse {
        private boolean exists;
    }

    @Getter
    @AllArgsConstructor
    public static class SignupResponse {
        private boolean success;
    }
}