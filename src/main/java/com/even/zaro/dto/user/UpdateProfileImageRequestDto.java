package com.even.zaro.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileImageRequestDto {
    @Schema(description = "프로필 이미지", example = "/images/profile/2-uuid.png")
    private String profileImage;
}
