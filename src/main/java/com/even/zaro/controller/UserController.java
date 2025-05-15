package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.user.UserInfoResponseDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.jwt.JwtUtil;
import com.even.zaro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getMyInfo(@AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        UserInfoResponseDto responseDto = userService.getMyInfo(userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회에 성공했습니다.", responseDto));
    }
}
