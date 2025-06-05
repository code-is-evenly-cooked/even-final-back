package com.even.zaro.healthcheck;

import com.even.zaro.global.ApiResponse;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "서버 및 DB 헬스 체크 API")
@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    private final MockRepository mockRepository;

    @Operation(summary = "서버 헬스 체크", description = "서버가 정상적으로 작동 중인지 확인합니다.")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("서버 정상 작동 중", null));
    }

    @Operation(summary = "DB 헬스 체크", description = "DB 연결 상태를 확인합니다.")
    @GetMapping("/health/db")
    public ResponseEntity<?> dbHealth() {
        try {
            mockRepository.count(); // 실제 쿼리로 DB 연결 확인
            return ResponseEntity.ok(ApiResponse.success("DB 연결 성공",null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(ErrorCode.DB_CONNECTION_FAILED.getHttpStatus())
                    .body(ErrorResponse.fail(ErrorCode.DB_CONNECTION_FAILED));
        }
    }
}
