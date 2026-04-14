package com.project.auth.service;

import com.project.auth.dto.domain.AuthEventType;
import com.project.auth.dto.event.AuthEvent;
import com.project.auth.dto.request.AuthRequest;
import com.project.auth.dto.response.MeResponse;
import com.project.auth.entity.AuthToken;
import com.project.auth.entity.User;
import com.project.auth.exception.AuthException;
import com.project.auth.kafka.KafkaProducerService;
import com.project.auth.repository.AuthTokenRepository;
import com.project.auth.repository.UserRepository;
import com.project.auth.security.JwtProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final KafkaProducerService kafkaProducerService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, AuthTokenRepository authTokenRepository, KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authTokenRepository = authTokenRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public boolean isDuplicate(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 회원가입
    public boolean signup(AuthRequest request) {

        if (userRepository.existsByUserId(request.getId())) {
            return false;
        }

        String hashed = passwordEncoder.encode(request.getPw());

        User user = new User();
        user.setUserId(request.getId());
        user.setPasswordHash(hashed);

        try {
            userRepository.save(user);

            kafkaProducerService.send(
                    new AuthEvent("auth-service", AuthEventType.SIGNUP_SUCCESS.name(), request.getId(), AuthEventType.SIGNUP_SUCCESS.getDescription())
            );

            return true;
        } catch(DataIntegrityViolationException e) {
            kafkaProducerService.send(
                    new AuthEvent("auth-service", AuthEventType.SIGNUP_FAIL.name(), request.getId(), AuthEventType.SIGNUP_FAIL.getDescription())
            );

            return false;
        } catch(Exception e) {
            kafkaProducerService.send(
                    new AuthEvent("auth-service", AuthEventType.SIGNUP_FAIL.name(), request.getId(), AuthEventType.SIGNUP_FAIL.getDescription())
            );

            return false;
        }
    }

    // 로그인
    public LoginResult login(AuthRequest request) {

        if (request.getId() == null || request.getId().isEmpty()) {
            throw new AuthException("아이디 입력 안됨", 400);
        }

        if (request.getPw() == null || request.getPw().isEmpty()) {
            throw new AuthException("비밀번호 입력 안됨", 400);
        }

        User user = userRepository.findByUserId(request.getId())
                .orElseThrow(() -> new AuthException("아이디 없음", 404));

        if (!passwordEncoder.matches(request.getPw(), user.getPasswordHash())) {
            throw new AuthException("비밀번호 틀림", 401);
        }

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);
        Date refreshExpiredAt = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 7); // 7일

        authTokenRepository.save(
                new AuthToken(user.getUid(), refreshToken, refreshExpiredAt)
        );

        return new LoginResult(accessToken, refreshToken);
    }

    public MeResponse getMe(String accessToken) {

        if (accessToken == null || accessToken.isEmpty()) {
            throw new AuthException("토큰 없음", 401);
        }

        String userId = jwtProvider.getUserId(accessToken);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException("유저 없음", 404));

        return new MeResponse(user.getUserId());
    }

    public void logout(String refreshToken) {

        AuthToken token = authTokenRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("토큰 없음"));

        token.setRevoked(true);

        authTokenRepository.save(token);
    }

    public String refresh(String refreshToken) {

        AuthToken token = authTokenRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("토큰 없음"));

        if (token.isRevoked()) {
            throw new RuntimeException("토큰 폐기됨");
        }

        if (token.getRefreshExpiredAt().before(new Date())) {
            throw new RuntimeException("토큰 만료");
        }

        User user = userRepository.findById(token.getUserUid())
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        return jwtProvider.createAccessToken(user);
    }
}

