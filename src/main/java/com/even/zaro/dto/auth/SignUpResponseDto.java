package com.even.zaro.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "회원가입 응답")
public class SignUpResponseDto {
    @Schema(description = "유저 ID", example = "1")
    private Long id;
    @Schema(description = "이메일", example = "test@naver.com")
    private String email;
    @Schema(description = "닉네임", example = "자취왕")
    private String nickname;
}
