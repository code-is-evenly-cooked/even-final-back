package com.even.zaro.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청")
public class SignUpRequestDto {
    @Schema(description = "이메일", example = "test@even.com", required = true)
    private String email;
    @Schema(description = "비밀번호", example = "Qwer1234!", required = true)
    private String password;
    @Schema(description = "닉네임", example = "자취왕", required = true)
    private String nickname;
}
