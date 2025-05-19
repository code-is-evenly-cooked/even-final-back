package com.even.zaro.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetEmailRequestDto {
    private String email;
}
