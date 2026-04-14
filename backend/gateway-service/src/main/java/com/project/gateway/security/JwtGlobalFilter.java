package com.project.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private final SecretKey SECRET = Keys.hmacShaKeyFor(
            "my-secret-key-my-secret-key-123456".getBytes(StandardCharsets.UTF_8)
    );

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        // 1. OPTIONS 무조건 통과
        if ("OPTIONS".equals(method)) {
            return chain.filter(exchange);
        }

        // 2. 인증 제외 (/auth → 로그인, 회원가입)
        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        // 3. internal → 헤더만 체크
        if (path.startsWith("/internal")) {

            String internalHeader = exchange.getRequest().getHeaders().getFirst("X-INTERNAL-CALL");

            if (!"true".equals(internalHeader)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        }

        // 4. 나머지 (/api/**) → JWT 검사
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();

            // 사용자 정보 전달
            exchange = exchange.mutate()
                    .request(r -> r.header("X-User-Id", userId))
                    .build();

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}