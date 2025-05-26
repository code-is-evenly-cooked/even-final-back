package com.even.zaro.controller;

import com.even.zaro.dto.favorite.FavoriteAddRequest;
import com.even.zaro.dto.favorite.FavoriteAddResponse;
import com.even.zaro.dto.favorite.FavoriteEditRequest;
import com.even.zaro.dto.favorite.FavoriteResponse;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "즐겨찾기", description = "즐겨찾기 API")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Operation(summary = "그룹에 즐겨찾기 추가", description = "그룹에 즐겨찾기를 추가합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/groups/{groupId}/favorites")
    public ResponseEntity<ApiResponse<FavoriteAddResponse>> addFavorite(@PathVariable("groupId") long groupId,
                                                                        @RequestBody FavoriteAddRequest request,
                                                                        @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        FavoriteAddResponse favoriteAddResponse = favoriteService.addFavorite(groupId, request, userInfoDto.getUserId());

        return ResponseEntity.ok(ApiResponse.success("그룹에 즐겨찾기가 추가되었습니다.", favoriteAddResponse));
    }


    @Operation(summary = "그룹의 즐겨찾기 조회", description = "그룹의 즐겨찾기 리스트를 조회합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{groupId}/items")
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getGroupItems(
            @PathVariable("groupId") long groupId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        List<FavoriteResponse> groupItems = favoriteService.getGroupItems(groupId);

        return ResponseEntity.ok(ApiResponse.success("즐겨찾기 그룹의 장소 목록을 성공적으로 조회했습니다.", groupItems));
    }


    @Operation(summary = "즐겨찾기 메모 수정", description = "즐겨찾기의 메모를 수정합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PatchMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<String>> editFavoriteMemo(
            @PathVariable("favoriteId") long favoriteId,
            @RequestBody FavoriteEditRequest request,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        favoriteService.editFavoriteMemo(favoriteId, request, userInfoDto.getUserId());

        return ResponseEntity.ok(ApiResponse.success("즐겨찾기 메모가 수정되었습니다."));
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기를 삭제합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<String>> deleteFavorite(
            @PathVariable("favoriteId") long favoriteId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        favoriteService.deleteFavorite(favoriteId, userInfoDto.getUserId());

        return ResponseEntity.ok(ApiResponse.success("즐겨찾기가 성공적으로 삭제되었습니다."));
    }
}
