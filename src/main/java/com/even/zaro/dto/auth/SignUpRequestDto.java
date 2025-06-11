package com.even.zaro.dto.auth;

import com.even.zaro.global.validator.annotation.ValidEmail;
import com.even.zaro.global.validator.annotation.ValidNickname;
import com.even.zaro.global.validator.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청")
public class SignUpRequestDto {

    @Schema(description = "이메일", example = "test@even.com", required = true)
    @NotBlank(message = "EMAIL_REQUIRED")
    @ValidEmail
    private String email;

    @Schema(description = "비밀번호", example = "Qwer1234!", required = true)
    @NotBlank(message = "PASSWORD_REQUIRED")
    @ValidPassword
    private String password;

    @Schema(description = "닉네임", example = "자취왕", required = true)
    @NotBlank(message = "NICKNAME_REQUIRED")
    @ValidNickname
    private String nickname;
}
