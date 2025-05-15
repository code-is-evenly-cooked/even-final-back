package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.user.UserInfoResponseDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.jwt.JwtUtil;
import com.even.zaro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "내 정보 조회", description = "AccessToken 기반으로 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getMyInfo(@AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        UserInfoResponseDto responseDto = userService.getMyInfo(userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회에 성공했습니다.", responseDto));
    }
}
