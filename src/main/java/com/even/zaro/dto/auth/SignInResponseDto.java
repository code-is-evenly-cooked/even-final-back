package com.even.zaro.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
}
