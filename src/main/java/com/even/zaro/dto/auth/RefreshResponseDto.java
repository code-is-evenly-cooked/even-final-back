package com.even.zaro.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RefreshResponseDto {
    private String accessToken;
}
