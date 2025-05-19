package com.even.zaro.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetEmailRequestDto {

    @Schema(description = "비밀번호 재설정을 요청할 사용자 이메일", example = "test@even.com")
    private String email;
}
