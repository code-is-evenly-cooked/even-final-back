package com.even.zaro.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerFollowingListDto {
    private Long userId;
    private String userName;
    private String profileImage;
}