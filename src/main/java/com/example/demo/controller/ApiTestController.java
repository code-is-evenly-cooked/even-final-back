package com.example.demo.controller;

import com.example.demo.global.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiTestController {

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> test() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "동훈");
        map.put("age", "27");

        return ResponseEntity.ok(ApiResponse.success(map, "성공"));
    }
}
