package com.even.zaro.unit.service;

import com.even.zaro.service.NotificationSseService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class NotificationSseServiceTest {

    @InjectMocks
    private NotificationSseService notificationSseService;

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

    }
}
