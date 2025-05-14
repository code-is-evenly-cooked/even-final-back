package com.even.zaro.controller;

import com.even.zaro.dto.UserProfileDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/{userID}")
    public ResponseEntity<?> getProfile(@PathVariable("userID") Long userID) {
        UserProfileDto profile = profileService.getUserProfile(userID);
        return ResponseEntity.ok(ApiResponse.success("유저 프로필 정보 조회 성공 !", profile));
    }
}
