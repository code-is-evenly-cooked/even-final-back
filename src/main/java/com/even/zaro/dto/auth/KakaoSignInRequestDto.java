package com.even.zaro.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoSignInRequestDto {

    @Schema(description = "카카오에서 받은 access token", example = "eyJhbGciOiJIUzI1...")
    private String accessToken;
}
