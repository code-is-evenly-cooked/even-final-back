package com.even.zaro.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Schema(description = "공통 페이징 응답 포맷")
public class PageResponse<T> {

    @ArraySchema(schema = @Schema(description = "데이터 목록"))
    private final List<T> content;

    @Schema(description = "전체 페이지 수", example = "5")
    private final int totalPages;

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private final int number;

    public PageResponse(List<T> content, int totalPages, int number) {
        this.content = content;
        this.totalPages = totalPages;
        this.number = number;
    }

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.number = page.getNumber();
    }
}
