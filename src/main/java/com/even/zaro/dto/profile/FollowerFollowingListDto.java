package com.even.zaro.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerFollowingListDto {

    @Schema(description = "사용자 ID", example = "88")
    private Long userId;

    @Schema(description = "사용자 이름", example = "권지용")
    private String userName;

    @Schema(description = "사용자 프로필 이미지 URL", example = "/images/profile/2-uuid.png")
    private String profileImage;
}
