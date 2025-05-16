package com.even.zaro.dto.profile;

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
    private Long userId;
    private String nickname;
    private String profileImage;
    private LocalDate liveAloneDate;
    private String mbti;
    private int postCount;
    private int followingCount;
    private int followerCount;
}
