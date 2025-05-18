package com.even.zaro.controller;

import com.even.zaro.dto.PresignedUrlResponse;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Image Upload - S3", description = "이미지 업로드 및 삭제 API")
public class S3Controller {

    private final S3Service s3Service;

    @Operation(
            summary = "Presigned URL 발급",
            description = """
        S3에 이미지 업로드를 위한 presigned URL을 발급합니다.
        - 발급된 URL은 `PUT` 방식으로 이미지 업로드에 사용됩니다.
        - Swagger에서는 실제 업로드 테스트가 불가능하므로, *반환된 URL을 Postman 등에서 직접 사용하세요.
    """, security = {@SecurityRequirement(name = "bearer-key")}
    )
    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(
            @Parameter(description = "업로드 유형 (profile | post)", example = "profile")
            @RequestParam String type,

            @Parameter(description = "사용자 ID (type=profile일 때 필수)", example = "1")
            @RequestParam(required = false) Long userId,

            @Parameter(description = "게시글 ID (type=post일 때 필수)", example = "10")
            @RequestParam(required = false) Long postId,

            @Parameter(description = "파일 확장자 (jpg, png, webp)", example = "jpg")
            @RequestParam String ext,

            @AuthenticationPrincipal JwtUserInfoDto user
            ) {
        PresignedUrlResponse response = s3Service.issuePresignedUrl(type, userId, postId, ext, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("presignedUrl을 발급했습니다.", response));
    }

    @Operation(
            summary = "이미지 삭제",
            description = """
            업로드된 이미지를 삭제합니다.
            - `key`는 presigned URL 응답에 포함된 S3 경로입니다.
            - 사용자는 자신의 프로필 이미지만 삭제할 수 있습니다.
        """, security = {@SecurityRequirement(name = "bearer-key")}
    )
    @DeleteMapping("/image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @Parameter(description = "삭제할 이미지 key", example = "images/profile/1-uuid.jpg")
            @RequestParam String key,

            @AuthenticationPrincipal JwtUserInfoDto user) {
        s3Service.deleteImage(key, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("이미지를 삭제했습니다.", null));
    }
}
