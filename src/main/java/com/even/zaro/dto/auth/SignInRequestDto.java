package com.even.zaro.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class SignInRequestDto {
    @Schema(description = "이메일", example = "test@even.com")
    private String email;

    @Schema(description = "비밀번호", example = "Qwer1234!")
    private String password;
}
