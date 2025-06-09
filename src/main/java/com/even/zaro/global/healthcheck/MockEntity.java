package com.even.zaro.global.healthcheck;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Schema(description = "Mock DB 연결 테스트용 엔티티")
@Entity
@Table(name = "health_check")
public class MockEntity {
    @Schema(description = "ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
