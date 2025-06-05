package com.even.zaro.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "댓글 응답 dto")
public class CommentResponseDto {

    @Schema(description = "댓글 ID", example = "1")
    private Long id;

    @Schema(description = "댓글 내용", example = "너무 좋네요!!")
    private String content;

    @Schema(description = "작성자 닉네임", example = "이브니")
    private String nickname;

    @Schema(description = "작성자 프로필 이미지", example = "/images/profile/2-uuid.png")
    private String profileImage;

    @Schema(description = "자취 시작일", example = "2024-03-01")
    private LocalDate liveAloneDate;

    @Schema(description = "댓글 작성 시간", example = "2025-05-21T05:33:00.111Z")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            timezone = "UTC"
    )
    private OffsetDateTime createdAt;

    @Schema(description = "댓글 수정 시간", example = "2025-05-22T23:15:12.111Z")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            timezone = "UTC"
    )
    private OffsetDateTime updatedAt;

    @Schema(description = "수정 여부", example = "true")
    @JsonProperty("isEdited")
    private boolean isEdited;

    @Schema(description = "댓글 작성자 여부", example = "true")
    @JsonProperty("isMine")
    private boolean isMine;

    @Schema(description = "멘션된 유저 정보", nullable = true,
            example = """
                {
                  "id": 2,
                  "nickname": "자취왕"
                }
                """)
    private MentionedUserDto mentionedUser;

    @JsonIgnore
    public boolean getMine() {
        return isMine;
    }

    @JsonIgnore
    public boolean getEdited() {
        return isEdited;
    }
}
