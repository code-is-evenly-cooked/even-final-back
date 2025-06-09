package com.even.zaro.unit.service;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.Notification;
import com.even.zaro.global.util.NotificationMapper;
import com.even.zaro.service.NotificationSseService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationSseServiceTest {

    @InjectMocks
    private NotificationSseService notificationSseService;

    @Mock
    private NotificationMapper notificationMapper;

    private final Long userId = 1L;

    @Nested
    class ConnectTest {
        @Test
        void connect_호출시_emitter_등록되고_초기이벤트_전송시도() {
            SseEmitter emitter = notificationSseService.connect(userId);

            assertThat(emitter).isNotNull();
        }
    }

    @Nested
    class SendTest {
        @Test
        void send_성공시_emitter에_이벤트_전송() throws IOException {
            Notification notification = createNotification();
            NotificationDto dto = createDto();

            // 수동으로 SseEmitter 생성 후 등록
            SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
            getEmittersMap().put(userId, emitter);

            when(notificationMapper.toDto(notification)).thenReturn(dto);

            notificationSseService.send(userId, notification); // 예외 안 나면 성공
        }


        private Notification createNotification() {
            return Notification.builder()
                    .id(1L)
                    .type(Notification.Type.FOLLOW)
                    .actorUserId(2L)
                    .targetId(3L)
                    .build();
        }

        private NotificationDto createDto() {
            return NotificationDto.builder()
                    .id(1L)
                    .type(Notification.Type.FOLLOW)
                    .build();
        }

        // NotificationSseService 내부의 private Map<Long, SseEmitter> emitters 필드에 접근
        // 리플렉션으로 접근해서 직접 조작
        @SuppressWarnings("unchecked")
        private Map<Long, SseEmitter> getEmittersMap() {
            try {
                var field = NotificationSseService.class.getDeclaredField("emitters");
                field.setAccessible(true);
                return (Map<Long, SseEmitter>) field.get(notificationSseService);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
