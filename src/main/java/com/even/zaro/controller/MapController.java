package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.MapService;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/map")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "지도", description = "지도 API")
public class MapController {
    private final MapService mapService;

    @Operation(summary = "장소의 정보와 유저들의 메모리스트 조회", description = "장소의 정보와 유저들의 메모리스트를 조회합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/place/{placeId}")
    public ResponseEntity<ApiResponse<MarkerInfoResponse>> getPlaceInfo(
            @PathVariable("placeId") long placeId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {

        MarkerInfoResponse placeInfo = mapService.getPlaceInfo(placeId);

        return ResponseEntity.ok(ApiResponse.success("해당 장소의 정보와 유저들의 메모리스트를 조회했습니다.", placeInfo));
    }
}
