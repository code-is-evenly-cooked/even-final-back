package com.even.zaro.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDto {
    @Schema(description = "비밀번호 재설정 토큰", example = "uuid-token-string")
    String token;

    @Schema(description = "새 비밀번호", example = "NewPassword123!")
    String newPassword;
}
