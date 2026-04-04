package com.project.auth.dto.domain;

public enum AuthEventType {
    SIGNUP("회원가입"),
    LOGIN_SUCCESS("로그인 성공"),
    LOGOUT("로그아웃"),
    LOGIN_FAIL("로그인 실패");

    private final String description;

    AuthEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
