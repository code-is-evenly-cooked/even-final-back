package com.even.zaro.service;

import com.even.zaro.dto.auth.KakaoUserInfoDto;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    public KakaoUserInfoDto getUserInfo(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfoDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new CustomException(ErrorCode.INVALID_OAUTH_TOKEN);
        }

    }
}
