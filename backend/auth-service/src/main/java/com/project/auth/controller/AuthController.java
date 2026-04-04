package com.project.auth.controller;

import com.project.auth.dto.request.AuthRequest;
import com.project.auth.dto.response.AuthResponse;
import com.project.auth.dto.response.LoginResponse;
import com.project.auth.dto.response.MeResponse;
import com.project.auth.service.AuthService;
import com.project.auth.service.LoginResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 아이디 중복 체크
    @GetMapping("/duplicate-id")
    public AuthResponse.DuplicateIdResponse duplicateId(@RequestParam String id) {
        // 중복 여부 조회
        boolean exists = authService.isDuplicate(id);

        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return new AuthResponse.DuplicateIdResponse(exists);
    }

    // 회원가입
    @PostMapping("/signup")
    public AuthResponse.SignupResponse signup(@RequestBody AuthRequest request) {
        boolean result = authService.signup(request);
        return new AuthResponse.SignupResponse(result);
    }

    // 로그인
    @PostMapping("/login")
    public LoginResponse login(@RequestBody AuthRequest request, HttpServletResponse response) {
        LoginResult result = authService.login(request);

        // refreshToken 쿠키 저장
        Cookie cookie = new Cookie("refreshToken", result.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);

        return new LoginResponse(result.getAccessToken());
    }

    @GetMapping("/me")
    public MeResponse me(HttpServletRequest request) {

        String accessToken = extractAccessToken(request);

        return authService.getMe(accessToken);
    }

    private String extractAccessToken(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        String refreshToken = extractRefreshTokenFromCookie(request);

        authService.logout(refreshToken);

        return ResponseEntity.ok().build();
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request) {

        String refreshToken = extractRefreshTokenFromCookie(request);

        String newAccessToken = authService.refresh(refreshToken);

        return ResponseEntity.ok(new LoginResponse(newAccessToken));
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}