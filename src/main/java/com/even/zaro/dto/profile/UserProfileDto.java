package com.even.zaro.dto.profile;

import com.even.zaro.entity.Mbti;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Schema(description = "사용자 프로필 이미지 URL", example = "/images/profile/2-uuid.png")
    private String profileImage;

    @Schema(description = "자취 시작일", example = "2023-05-01")
    private LocalDate liveAloneDate;

    @Schema(description = "사용자 MBTI", example = "ENTP")
    private Mbti mbti;

    @Schema(description = "작성한 게시글 수", example = "10")
    private int postCount;

    @Schema(description = "팔로잉 수", example = "5")
    private int followingCount;

    @Schema(description = "팔로워 수", example = "999")
    private int followerCount;

    @Schema(description = "프로필 주인 여부", example = "true")
    @JsonProperty("isMine")
    private boolean isMine;

    @Schema(description = "프로필 팔로우 여부", example = "false")
    @JsonProperty("isFollowing")
    private boolean isFollowing;

    @JsonIgnore
    public boolean getMine() {
        return isMine;
    }

    @JsonIgnore
    public boolean getFollowing() {
        return isFollowing;
    }
}
