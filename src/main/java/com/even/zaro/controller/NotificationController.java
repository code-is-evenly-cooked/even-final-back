package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림", description = "로그인 된 사용자 알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "유저 알림 list 조회 (인증 필요)",
            description = "로그인 된 유저의 알림 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping
    public ResponseEntity<?> getNotificationsList(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        List<NotificationDto> notifications = notificationService.getNotificationsList(userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("로그인 한 사용자의 알림 목록 조회 성공 !", notifications));
    }

    @PatchMapping("/api/notifications/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> readNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo
    ) {
        notificationService.markAsRead(notificationId, userInfo.getUserId());
        return ResponseEntity.ok(ApiResponse.success("선택한 알림 읽음 처리 성공 !"));
    }
}
