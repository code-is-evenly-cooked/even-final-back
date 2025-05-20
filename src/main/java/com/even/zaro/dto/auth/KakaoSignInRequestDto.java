package com.even.zaro.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoSignInRequestDto {
    @JsonProperty("accessToken")
    private String kakaoToken;
}
