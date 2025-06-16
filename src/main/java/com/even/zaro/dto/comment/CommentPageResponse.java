package com.even.zaro.dto.comment;

import com.even.zaro.dto.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Schema(description = "댓글 리스트 페이지 응답 dto")
public class CommentPageResponse extends PageResponse<CommentResponseDto> {

    @Schema(description = "전체 댓글 개수", example = "10")
    private int totalComments;

    public CommentPageResponse(Page<CommentResponseDto> page, int totalComments) {
        super(page);
        this.totalComments = totalComments;
    }

    public CommentPageResponse(List<CommentResponseDto> content, int totalPages, int number, int totalComments) {
        super(content, totalPages, number);
        this.totalComments = totalComments;
    }
}
