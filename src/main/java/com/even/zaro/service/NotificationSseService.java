package com.even.zaro.service;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.Notification;
import com.even.zaro.global.util.NotificationMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSseService {

    private static class EmitterInfo {
        private final SseEmitter emitter;
        private Instant lastEventTime;

        public EmitterInfo(SseEmitter emitter) {
            this.emitter = emitter;
            this.lastEventTime = Instant.now();
        }

        public SseEmitter getEmitter() {
            return emitter;
        }

        public void updateLastEventTime() {
            this.lastEventTime = Instant.now();
        }
    }

    private final Map<Long, EmitterInfo> emitters = new ConcurrentHashMap<>();
    private final NotificationMapper notificationMapper;

    @PostConstruct
    public void startPingTask() {
        log.info("[SSE] Ping 작업 초기화 완료");
    }

    // 30초마다 모든 클라이언트에게 ping 이벤트 전송
    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void sendPingToAllClients() {
        for (Iterator<Map.Entry<Long, EmitterInfo>> it = emitters.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Long, EmitterInfo> entry = it.next();
            Long userId = entry.getKey();
            EmitterInfo info = entry.getValue();
            try {
                info.getEmitter().send(SseEmitter.event().name("ping").data("keep-alive"));
                info.updateLastEventTime();
                log.debug("[SSE] 유저 {} 에게 ping 전송", userId);
            } catch (IOException e) {
                log.warn("[SSE] 유저 {} ping 전송 실패 → emitter 제거", userId);
                it.remove();
            }
        }
    }

    public SseEmitter connect(Long userId) {
        log.info("[SSE] 유저 {} 연결 시도", userId);
        long timeout = 10800000L; // 클라이언트와 동일하게 timeout 3시간으로 설정

        SseEmitter emitter = new SseEmitter(timeout);
        EmitterInfo emitterInfo = new EmitterInfo(emitter);
        emitters.put(userId, emitterInfo);

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
        EmitterInfo info = emitters.get(userId);

        if (info != null) {
            try {
                NotificationDto dto = notificationMapper.toDto(notification);
                info.getEmitter().send(SseEmitter.event().name("notification").data(dto));
                info.updateLastEventTime();
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
