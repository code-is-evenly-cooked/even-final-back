package com.even.zaro.controller;

import com.even.zaro.dto.UserPostDto;
import com.even.zaro.dto.UserProfileDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    // 유저 기본 프로필 조회
    @GetMapping("/{userID}")
    public ResponseEntity<?> getUserProfile(@PathVariable("userID") Long userID) {
        try {
            UserProfileDto profile = profileService.getUserProfile(userID);
            return ResponseEntity.ok(ApiResponse.success("유저 프로필 정보 조회 성공 !", profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(ErrorCode.USER_EXCEPTION));
        }
    }

    // 유저가 쓴 게시물 list 조회
    @GetMapping("/{userID}/posts")
    public ResponseEntity<?> getUserPosts(
            @PathVariable("userID") Long userID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<UserPostDto> posts = profileService.getUserPosts(userID, pageRequest);

            return ResponseEntity.ok(ApiResponse.success("사용자 게시글 조회 성공 !", Map.of(
                    "content", posts.getContent(),
                    "totalPages", posts.getTotalPages()
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(ErrorCode.USER_EXCEPTION));
        }
    }

    // 유저가 좋아요 누른 게시물 list 조회
    @GetMapping("/{userID}/likes")
    public ResponseEntity<?> getUserLikedPosts(
            @PathVariable("userID") Long userID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserPostDto> likedPosts = profileService.getUserLikedPosts(userID, pageRequest);

        return ResponseEntity.ok(ApiResponse.success("사용자가 좋아요 한 게시글 조회 성공 !", Map.of(
                "content", likedPosts.getContent(),
                "totalPages", likedPosts.getTotalPages()
        )));
    }
}
