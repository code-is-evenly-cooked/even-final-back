package com.even.zaro.controller;

import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.post.PostPreviewDto;
import com.even.zaro.dto.post.PostSearchDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.PostEsSearchService;
import com.even.zaro.service.PostSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@Tag(name = "게시글 검색 API", description = "카테고리 + 키워드 기반 게시글 검색 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class PostSearchController {

    private final PostSearchService postSearchService;
    private final PostEsSearchService postEsSearchService;

    @Operation(summary = "게시글 검색", description = "카테고리 및 키워드 기반으로 게시글을 검색합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PostSearchDto>>> searchPosts(
            @RequestParam String category,
            @RequestParam String keyword,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        PageResponse<PostSearchDto> results = postSearchService.searchPosts(category, keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success("검색 결과입니다.", results));
    }

    @Operation(summary = "ES 게시글 검색", description = "Elasticsearch 기반의 게시글 검색 결과를 페이징 형태로 제공합니다.")
    @GetMapping("/es")
    public ResponseEntity<ApiResponse<PageResponse<PostSearchDto>>> searchEsPosts(
            @RequestParam(required = false) String category,
            @RequestParam String keyword,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) throws IOException {
        PageResponse<PostSearchDto> result = postEsSearchService.searchWithPage(category, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("ES 검색 결과입니다." , result));
    }
}