package com.even.zaro.controller;

import com.even.zaro.dto.profileDto.CreateGroupRequest;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@Slf4j
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/group")
    public ResponseEntity<ApiResponse<String>> createGroup(@RequestBody CreateGroupRequest request) {
        profileService.createGroup(request);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 그룹이 생성되었습니다."));
    }

}
