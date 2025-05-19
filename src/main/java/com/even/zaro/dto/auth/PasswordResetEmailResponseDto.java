package com.even.zaro.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetEmailResponseDto {
    @Schema(description = "비밀번호 재설정 메일이 전송된 이메일", example = "test@even.com")
    private String email;
}
