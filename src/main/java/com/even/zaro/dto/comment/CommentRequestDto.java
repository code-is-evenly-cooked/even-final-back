package com.even.zaro.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {

    @Schema(description = "댓글 내용/수정 내용", example = "너무 좋네요!!")
    private String content;

    @Schema(description = "멘션 대상 닉네임", example = "자취왕")
    private String mentionedNickname;
}
