package com.even.zaro.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class SignUpRequestDto {
    @NotBlank
    private String email;

    @NotBlank
    private String nickname;

    private String password;
}
