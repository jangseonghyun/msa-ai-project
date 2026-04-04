package com.project.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "auth_token")
@Getter
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_uid")
    private Long userUid;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Setter
    @Column(name = "revoked")
    private boolean revoked = false;

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "refresh_expired_at")
    private Date refreshExpiredAt;

    // 🔥 생성자
    public AuthToken(Long userUid, String refreshToken, Date refreshExpiredAt) {
        this.userUid = userUid;
        this.refreshToken = refreshToken;
        this.refreshExpiredAt = refreshExpiredAt;
    }

    protected AuthToken() {}

}