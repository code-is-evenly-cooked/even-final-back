package com.even.zaro.dto.auth;

import com.even.zaro.entity.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class SignInResponseDto {
    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "회원 ID", example = "2")
    private Long userId;

    @Schema(description = "이메일", example = "test@even.com")
    private String email;

    @Schema(description = "닉네임", example = "이브니쨩")
    private String nickname;

    @Schema(description = "프로필 이미지", example = "/images/profile/2-uuid.png")
    private String profileImage;

    @Schema(description = "로그인 방식(LOCAL or KAKAO)", example = "LOCAL")
    private Provider provider;
}
