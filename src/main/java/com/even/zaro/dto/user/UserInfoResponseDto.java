package com.even.zaro.dto.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserInfoResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private LocalDate birthday;
    private String gender;
    private String mbti;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private LocalDate liveAloneDate;
    private String provider;
}
