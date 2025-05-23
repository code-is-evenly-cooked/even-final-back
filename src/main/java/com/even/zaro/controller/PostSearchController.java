package com.even.zaro.controller;

import com.even.zaro.dto.post.PostPreviewDto;
import com.even.zaro.dto.post.PostSearchDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.PostSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class PostSearchController {

    private final PostSearchService postSearchService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> searchPosts(
            @RequestParam String category,
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<PostSearchDto> results = postSearchService.searchPosts(category, keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success("검색 결과입니다.", Map.of(
                "content", results.getContent(),
                "totalPages", results.getTotalPages(),
                "currentPage", results.getNumber() + 1,
                "totalElements", results.getTotalElements()
        )));
    }
}