package com.even.zaro.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "password_reset_token")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String token;
    private LocalDateTime expiredAt;
    private boolean used;

    public PasswordResetToken(String email, String token, LocalDateTime expiredAt, boolean used) {
        this.email = email;
        this.token = token;
        this.expiredAt = expiredAt;
        this.used = used;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiredAt);
    }

    public void markUsed() {
        this.used = true;
    }
}
