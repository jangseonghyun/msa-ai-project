package com.project.auth.dto.domain;

public enum AuthEventType {
    SIGNUP_SUCCESS("회원가입 성공"),
    SIGNUP_FAIL("회원가입 실패"),
    LOGIN_SUCCESS("로그인 성공"),
    LOGIN_FAIL("로그인 실패"),
    LOGOUT("로그아웃");


    private final String description;

    AuthEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
