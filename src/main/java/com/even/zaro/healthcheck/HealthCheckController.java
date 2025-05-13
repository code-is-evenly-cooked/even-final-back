package com.even.zaro.healthcheck;

import com.even.zaro.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    private final MockRepository mockRepository;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("OK", "서버 정상 작동 중"));
    }

    @GetMapping("/health/db")
    public ResponseEntity<ApiResponse<String>> dbHealth() {
        try {
            mockRepository.count(); // 실제 쿼리로 DB 연결 확인
            return ResponseEntity.ok(ApiResponse.success("DB 연결 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("DB 연결 실패"));
        }
    }
}
