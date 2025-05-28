package com.even.zaro.service;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSseService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(Long userId) {
        log.info("[SSE] 유저 {} 연결 시도", userId);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            log.info("[SSE] 유저 {} 연결 종료 (onCompletion)", userId);
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.info("[SSE] 유저 {} 연결 타임아웃 (onTimeout)", userId);
            emitters.remove(userId);
        });

        emitter.onError((e) -> {
            log.warn("[SSE] 유저 {} 오류 발생: {}", userId, e.getMessage());
            emitters.remove(userId);
        });

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 완료"));
            log.info("[SSE] 유저 {} 연결 성공 및 초기 이벤트 전송", userId);
        } catch (IOException e) {
            log.error("[SSE] 유저 {} 연결 초기 이벤트 전송 실패: {}", userId, e.getMessage());
            emitters.remove(userId);
        }

        return emitter;
    }

    public void send(Long userId, Notification notification) {
        log.info("[SSE] 유저 {} 에게 알림 전송 시도", userId);
        SseEmitter emitter = emitters.get(userId);

        if (emitter != null) {
            try {
                NotificationDto dto = NotificationDto.builder()
                        .id(notification.getId())
                        .type(notification.getType())
                        .targetId(notification.getTargetId())
                        .isRead(notification.isRead())
                        .createdAt(notification.getCreatedAt())
                        .build();

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto));
                log.info("[SSE] 유저 {} 에게 알림 전송 성공: {}", userId, dto);
            } catch (IOException e) {
                log.error("[SSE] 유저 {} 에게 알림 전송 실패: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        } else {
            log.warn("[SSE] 유저 {} 는 현재 연결되지 않음. emitter 없음!", userId);
        }
    }
}

