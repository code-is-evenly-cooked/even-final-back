package com.even.zaro.dto.notification;

import com.even.zaro.entity.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    @Schema(description = "알림 ID (pk)", example = "32")
    private Long id;

    @Schema(description = "알림 종류 (LIKE,COMMENT,FOLLOW)", example = "LIKE")
    private Notification.Type type;

    @Schema(description = "LIKE → 해당 게시글의 post_id, COMMENT → 해당 댓글의 comment_id, FOLLOW → follower_id", example = "22")
    private Long targetId;

    @Schema(description = "알림 읽음처리 여부", example = "false")
    private boolean isRead;

    @Schema(description = "알림 생성 시각", example = "2025-05-09T12:00:00.111Z")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            timezone = "UTC"
    )
    private OffsetDateTime createdAt;

    // 알림 발생 주체 유저 정보

    @Schema(description = "알림 발생 주체 유저 ID", example = "88")
    private Long actorId;

    @Schema(description = "알림 발생 주체 유저 닉네임", example = "맛잘알")
    private String actorName;

    @Schema(description = "알림 발생 주체 프로필 이미지", example = "/images/profile/uuid.png")
    private String actorProfileImage;

    // 타입별 필요 정보

    @Schema(description = "게시글 ID", example = "2")
    private Long postId;

    @Schema(description = "게시글 카테고리", example = "TOGETHER")
    private String category;

    @Schema(description = "게시글 썸네일", example = "/images/post/thumb.png")
    private String thumbnailImage;

    @Schema(description = "댓글 내용", example = "레시피 공유 가능할까요??")
    private String comment;
}