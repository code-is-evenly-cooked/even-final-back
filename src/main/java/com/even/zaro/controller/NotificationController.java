package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getNotificationsList(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        List<NotificationDto> notifications = notificationService.getNotificationsList(userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("로그인 한 사용자의 알림 목록 조회 성공 !", notifications));
    }
}
