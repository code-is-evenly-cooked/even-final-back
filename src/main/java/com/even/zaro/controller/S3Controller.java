package com.even.zaro.controller;

import com.even.zaro.dto.PresignedUrlResponse;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(
            @RequestParam String type,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long postId,
            @RequestParam String ext,
            @AuthenticationPrincipal JwtUserInfoDto user
            ) {
        PresignedUrlResponse response = s3Service.issuePresignedUrl(type, userId, postId, ext);
        return ResponseEntity.ok(ApiResponse.success("presignedUrl을 발급했습니다.", response));
    }

    @DeleteMapping("/image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@RequestParam String key, @AuthenticationPrincipal JwtUserInfoDto user) {
        s3Service.deleteImage(key, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("이미지를 삭제했습니다.", null));
    }
}
