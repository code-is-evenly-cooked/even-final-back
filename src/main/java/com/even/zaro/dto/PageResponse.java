package com.even.zaro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse<T> {

    private final List<T> content;

    private final int totalPages;

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
