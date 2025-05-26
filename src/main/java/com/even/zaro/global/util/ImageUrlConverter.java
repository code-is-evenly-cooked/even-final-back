package com.even.zaro.global.util;

import com.even.zaro.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUrlConverter {
    private final S3Service s3Service;

    public String getURL(String key) {
        return key != null ? s3Service.getUrl(key) : null;
    }
}
