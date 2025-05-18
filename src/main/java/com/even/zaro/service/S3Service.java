package com.even.zaro.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.even.zaro.dto.PresignedUrlResponse;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import com.even.zaro.global.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final long MINUTE = 1000 * 60;
    private static final List<String> ALLOWED_EXTS = List.of("jpg", "jpeg", "png", "webp");

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String generateProfileKey(Long userId, String ext) {
        return "images/profile/" + userId + "-" + UUID.randomUUID() + "." + ext;
    }

    public String generatePostImageKey(Long postId, String ext) {
        return "images/post/" + postId + "-" + UUID.randomUUID() + "." + ext;
    }

    public String generatePresignedUrl(String key) {
        Date expiration = new Date(System.currentTimeMillis() + MINUTE * 10); // 호출 시점 기준

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        return s3Client.generatePresignedUrl(request).toString();
    }

    public void deleteImage(String key, Long currentUserId) {
        if (key.startsWith("images/profile/")) {
            String ownerId = key.split("/")[2].split("-")[0];
            if (!ownerId.equals(currentUserId.toString())) {
                throw new UserException(ErrorCode.UNAUTHORIZED_IMAGE_DELETE);
            }
        }

        // post 작성 중 취소시 삭제 과정 - 프론트 처리
        s3Client.deleteObject(bucket, key);
    }

    public PresignedUrlResponse issuePresignedUrl(String type, Long userId, Long postId, String ext) {
        validateExtension(ext);

        String key = switch (type) {
            case "profile" -> {
                if (userId == null) throw new CustomException(ErrorCode.INVALID_USER_ID);
                yield generateProfileKey(userId, ext);
            }
            case "post" -> {
                if (postId == null) throw new CustomException(ErrorCode.INVALID_POST_ID);
                yield generatePostImageKey(postId, ext);
            }
            default -> throw new CustomException(ErrorCode.INVALID_UPLOAD_TYPE);
        };

        String url = generatePresignedUrl(key);
        return new PresignedUrlResponse(url, key);
    }

    private void validateExtension(String ext) {
        if (ext == null || !ALLOWED_EXTS.contains(ext.toLowerCase())) {
            throw new CustomException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }
}
