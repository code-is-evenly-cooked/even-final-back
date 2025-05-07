package com.evenly.jachui.controller;

import com.evenly.jachui.global.ApiResponse;
import com.evenly.jachui.dto.ExDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "API 테스트 컨트롤러")
public class ApiTestController {

    @Operation(summary= "테스트 API", description = "테스트 API 컨트롤러입니다.")
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<ExDTO>> test() {
        ExDTO exDto = ExDTO.builder()
                .id(1)
                .username("동훈")
                .build();

        return ResponseEntity.ok(ApiResponse.success(exDto, "성공"));
    }
}
