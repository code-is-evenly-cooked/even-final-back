package com.even.zaro.dto.notification;

import com.even.zaro.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationSseDto {

    @Schema(description = "알림 ID (pk)", example = "32")
    private Long id;

    @Schema(description = "알림 종류 (LIKE,COMMENT,FOLLOW)", example = "LIKE")
    private Notification.Type type;

    @Schema(description = "LIKE → 해당 게시글의 post_id, COMMENT → 해당 댓글의 comment_id, FOLLOW → follower_id", example = "22")
    private Long targetId;

    @Schema(description = "알림 읽음처리 여부", example = "false")
    private boolean isRead;

    @Schema(description = "알림 생성 시각", example = "2025-05-09T12:00:00")
    private LocalDateTime createdAt;
}
