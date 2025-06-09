package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.profile.*;
import com.even.zaro.service.ProfileService;

import com.even.zaro.global.ApiResponse;
import com.even.zaro.global.jwt.JwtUtil;
import com.even.zaro.dto.PageResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "프로필 페이지", description = "프로필 페이지 API")
public class ProfileController {
    private final ProfileService profileService;

    private final JwtUtil jwtUtil;
  
    // 유저 기본 프로필 조회 (Profile 기능들 중 유일하게 인증 불필요 !)
    @Operation(
            summary = "유저 기본 프로필 조회 (인증 불필요)",
            description = "특정 유저의 프로필 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(
            @Parameter(description = "조회할 유저의 ID") @PathVariable("userId") Long userId) {
        UserProfileDto profile = profileService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("유저 프로필 정보 조회 성공 !", profile));
    }

    // 유저가 쓴 게시물 list 조회
    @Operation(
            summary = "유저가 쓴 게시글 list 조회 (인증 필요)",
            description = "특정 유저가 작성한 게시글 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @GetMapping("/{userId}/posts")
    public ResponseEntity<?> getUserPosts(
            @Parameter(description = "조회할 유저의 ID") @PathVariable("userId") Long userId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        PageResponse<UserPostDto> posts = profileService.getUserPosts(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("사용자 게시글 조회 성공 !", posts));
    }

    // 유저가 좋아요 누른 게시물 list 조회
    @Operation(
            summary = "유저가 좋아요 한 게시글 list 조회 (인증 필요)",
            description = "특정 유저가 좋아요 한 게시글 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @GetMapping("/{userId}/likes")
    public ResponseEntity<?> getUserLikedPosts(
            @Parameter(description = "조회할 유저의 ID") @PathVariable("userId") Long userId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        PageResponse<UserPostDto> likedPosts = profileService.getUserLikedPosts(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("사용자가 좋아요 한 게시글 조회 성공 !", likedPosts));
    }

    // 유저가 쓴 댓글 list 조회
    @Operation(
            summary = "유저가 쓴 댓글 list 조회 (인증 필요)",
            description = "특정 유저가 쓴 댓글 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @GetMapping("/{userId}/comments")
    public ResponseEntity<?> getUserComments(
            @Parameter(description = "조회할 유저의 ID") @PathVariable("userId") Long userId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        PageResponse<UserCommentDto> comments = profileService.getUserComments(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("사용자가 작성한 댓글 조회 성공 !", comments));
    }

    // 다른 유저 팔로우 하기
    @Operation(
            summary = "팔로우 하기 (인증 필요)",
            description = "로그인 된 사용자의 AccessToken 기반으로, 다른 사용자를 팔로우합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/{userId}/follow")
    public ResponseEntity<?> followUser(
            @Parameter(description = "팔로우할 유저 ID") @PathVariable("userId") Long userId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        profileService.followUser(userInfoDto.getUserId(), userId);
        return ResponseEntity.ok(ApiResponse.success("로그인 된 유저가 타겟 유저 팔로우 성공 !"));
    }

    // 다른 유저 언팔로우 하기
    @Operation(
            summary = "언팔로우 하기 (인증 필요)",
            description = "로그인 된 사용자의 AccessToken 기반으로, 다른 사용자를 언팔로우합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<?> unfollowUser(
            @Parameter(description = "언팔로우할 유저 ID") @PathVariable("userId") Long userId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        profileService.unfollowUser(userInfoDto.getUserId(), userId);
        return ResponseEntity.ok(ApiResponse.success("로그인 된 유저가 타겟 유저 언팔로우 성공 !"));
    }

    // 팔로잉 목록 조회
    @Operation(
            summary = "팔로잉 목록 조회 (인증 필요)",
            description = "특정 유저가 팔로우한 사용자 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{userId}/followings")
    public ResponseEntity<?> getUserFollowings(
            @Parameter(description = "조회할 유저의 ID") @PathVariable Long userId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        List<FollowerFollowingListDto> followings = profileService.getUserFollowings(userId);
        return ResponseEntity.ok(ApiResponse.success("유저의 팔로잉 목록 조회 성공 !", followings));
    }

    // 팔로워 목록 조회
    @Operation(
            summary = "팔로워 목록 조회 (인증 필요)",
            description = "특정 유저를 팔로우하는 사용자 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getUserFollowers(
            @Parameter(description = "조회할 유저의 ID") @PathVariable Long userId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        List<FollowerFollowingListDto> followers = profileService.getUserFollowers(userId);
        return ResponseEntity.ok(ApiResponse.success("유저의 팔로워 목록 조회 성공 !", followers));
    }
}
