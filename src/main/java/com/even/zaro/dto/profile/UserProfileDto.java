package com.even.zaro.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    @Schema(description = "사용자 ID", example = "32")
    private Long userId;

    @Schema(description = "사용자 닉네임", example = "카리나")
    private String nickname;

    @Schema(description = "사용자 프로필 이미지 URL", example = "/images/user32.jpg")
    private String profileImage;

    @Schema(description = "자취 시작일", example = "2023-05-01")
    private LocalDate liveAloneDate;

    @Schema(description = "사용자 MBTI", example = "ENTP")
    private String mbti;

    @Schema(description = "작성한 게시글 수", example = "10")
    private int postCount;

    @Schema(description = "팔로잉 수", example = "5")
    private int followingCount;

    @Schema(description = "팔로워 수", example = "999")
    private int followerCount;
}
