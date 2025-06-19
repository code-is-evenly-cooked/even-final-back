package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.dto.map.PlaceResponse;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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


    @Operation(summary = "사용자 위치 기반 인근 맛집? 조회", description = "사용자의 위치를 이용해 인근 맛집 또는 장소를 조회합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/place")
    public ResponseEntity<ApiResponse<PlaceResponse>> getPlacesByCoordinate(
            @Parameter(description = "사용자의 현재 위도", example = "37.554722")
            @RequestParam double lat,

            @Parameter(description = "사용자의 현재 경도", example = "126.970833")
            @RequestParam double lng,

            @Parameter(description = "조회 반경 (단위: km)", example = "1.0")
            @RequestParam double distanceKm) {

        PlaceResponse placesByCoordinate = mapService.getPlacesByCoordinate(lat, lng, distanceKm);

        if (placesByCoordinate.getTotalCount() == 0) {
            return ResponseEntity.ok(ApiResponse.success("인근에 조회된 장소가 없습니다.", placesByCoordinate));
        }
        return ResponseEntity.ok(ApiResponse.success("인근 장소 리스트를 성공적으로 조회했습니다.", placesByCoordinate));
    }
}
