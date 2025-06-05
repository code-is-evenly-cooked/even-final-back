package com.even.zaro.dto.user;

import com.even.zaro.entity.Gender;
import com.even.zaro.entity.Mbti;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class UserInfoResponseDto {
    @Schema(description = "유저 ID", example = "1")
    private Long userId;

    @Schema(description = "이메일", example = "test@even.com")
    private String email;

    @Schema(description = "닉네임", example = "이브니쨩")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "/images/profile/2-uuid.png")
    private String profileImage;

    @Schema(description = "생일", example = "1997-05-15", nullable = true)
    private LocalDate birthday;

    @Schema(description = "자취 시작일", example = "2024-01-01", nullable = true)
    private LocalDate liveAloneDate;

    @Schema(description = "성별", example = "MALE", nullable = true)
    private Gender gender;

    @Schema(description = "MBTI", example = "INFP", nullable = true)
    private Mbti mbti;

    @Schema(description = "계정 생성일", example = "2024-01-01T12:00:00")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            timezone = "UTC"
    )
    private OffsetDateTime createdAt;

    @Schema(description = "계정 수정일", example = "2024-04-01T18:20:00")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            timezone = "UTC"
    )
    private OffsetDateTime updatedAt;

    @Schema(description = "마지막 로그인 일시", example = "2025-05-15T12:30:00")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            timezone = "UTC"
    )
    private OffsetDateTime lastLoginAt;

    @Schema(description = "회원가입 경로", example = "LOCAL")
    private String provider;

    @Schema(description = "이메일 인증 여부", example = "true")
    @JsonProperty("isValidated")
    private boolean isValidated;

    @JsonIgnore
    public boolean getValidated() {
        return isValidated;
    }
}
