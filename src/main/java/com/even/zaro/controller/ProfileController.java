package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.profile.*;
import com.even.zaro.service.ProfileService;

import com.even.zaro.global.ApiResponse;
import com.even.zaro.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "프로필 페이지", description = "프로필 페이지 API")
public class ProfileController {
    private final ProfileService profileService;

    private final JwtUtil jwtUtil;
  
    // 유저 기본 프로필 조회
    @Operation(summary = "유저 기본 프로필 조회", description = "특정 유저의 프로필 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(
            @Parameter(description = "조회할 유저의 ID") @PathVariable("userId") Long userId) {
        UserProfileDto profile = profileService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("유저 프로필 정보 조회 성공 !", profile));
    }

    // 유저가 쓴 게시물 list 조회
    @Operation(summary = "유저가 쓴 게시글 조회", description = "특정 유저가 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/{userId}/posts")
    public ResponseEntity<?> getUserPosts(
            @Parameter(description = "조회할 유저의 ID") @PathVariable("userId") Long userId,
            @Parameter(description = "페이지 번호 (기본값: 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserPostDto> posts = profileService.getUserPosts(userId, pageRequest);
        return ResponseEntity.ok(ApiResponse.success("사용자 게시글 조회 성공 !", Map.of(
                "content", posts.getContent(),
                "totalPages", posts.getTotalPages()
        )));
    }

    // 유저가 좋아요 누른 게시물 list 조회
    @GetMapping("/{userId}/likes")
    public ResponseEntity<?> getUserLikedPosts(
            @Parameter(description = "조회할 유저의 ID") @PathVariable("userId") Long userId,
            @Parameter(description = "페이지 번호 (기본값: 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserPostDto> likedPosts = profileService.getUserLikedPosts(userId, pageRequest);
        return ResponseEntity.ok(ApiResponse.success("사용자가 좋아요 한 게시글 조회 성공 !", Map.of(
                "content", likedPosts.getContent(),
                "totalPages", likedPosts.getTotalPages()
        )));
    }

    // 유저가 쓴 댓글 list 조회
    @GetMapping("/{userId}/comments")
    public ResponseEntity<?> getUserComments(
            @Parameter(description = "조회할 유저의 ID") @PathVariable("userId") Long userId,
            @Parameter(description = "페이지 번호 (기본값: 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserCommentDto> comments = profileService.getUserComments(userId, pageRequest);

        return ResponseEntity.ok(ApiResponse.success("사용자가 작성한 댓글 조회 성공 !", Map.of(
                        "content", comments.getContent(),
                        "totalPages", comments.getTotalPages()
                )));
    }

    // 다른 유저 팔로우 하기
    @Operation(summary = "(로그인 된 상태) 팔로우 하기", description = "AccessToken 기반으로 다른 사용자를 팔로우합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/{userId}/follow")
    public ResponseEntity<?> followUser(
            @Parameter(description = "팔로우할 유저 ID") @PathVariable("userId") Long userId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        profileService.followUser(userInfoDto.getUserId(), userId);
        return ResponseEntity.ok(ApiResponse.success("로그인 된 유저가 타겟 유저 팔로우 성공 !"));
    }

    // 다른 유저 언팔로우 하기
    @Operation(summary = "(로그인 된 상태) 언팔로우 하기", description = "AccessToken 기반으로 다른 사용자를 언팔로우합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<?> unfollowUser(
            @Parameter(description = "언팔로우할 유저 ID") @PathVariable("userId") Long userId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        profileService.unfollowUser(userInfoDto.getUserId(), userId);
        return ResponseEntity.ok(ApiResponse.success("로그인 된 유저가 타겟 유저 언팔로우 성공 !"));
    }

    // 팔로잉 목록 조회
    @Operation(summary = "팔로잉 목록 조회", description = "특정 유저가 팔로우한 사용자 목록을 조회합니다.")
    @GetMapping("/{userId}/followings")
    public ResponseEntity<?> getUserFollowings(
            @Parameter(description = "조회할 유저의 ID") @PathVariable Long userId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        List<FollowerFollowingListDto> followings = profileService.getUserFollowings(userId);
        return ResponseEntity.ok(ApiResponse.success("유저의 팔로잉 목록 조회 성공 !", followings));
    }

    // 팔로워 목록 조회
    @Operation(summary = "팔로워 목록 조회", description = "특정 유저를 팔로우하는 사용자 목록을 조회합니다.")
    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getUserFollowers(
            @Parameter(description = "조회할 유저의 ID") @PathVariable Long userId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        List<FollowerFollowingListDto> followers = profileService.getUserFollowers(userId);
        return ResponseEntity.ok(ApiResponse.success("유저의 팔로워 목록 조회 성공 !", followers));
    }
}
