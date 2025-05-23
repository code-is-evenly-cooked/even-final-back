package com.even.zaro.service;

import com.even.zaro.dto.post.PostSearchDto;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.repository.PostSearchRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostSearchService {
    private final PostSearchRepositoryCustom postSearchRepository;

    public Page<PostSearchDto> searchPosts(String category, String keyword, Pageable pageable) {

        if (category == null || category.isBlank()) {
            throw new PostException(ErrorCode.CATEGORY_REQUIRED);
        }
        if (keyword == null || keyword.isBlank()) {
            throw new PostException(ErrorCode.KEYWORD_REQUIRED);
        }

        Page<PostSearchDto> result = postSearchRepository.searchPosts(category, keyword, pageable);

        if (result.isEmpty()) {
            throw new PostException(ErrorCode.POST_NOT_FOUND);
        }

        return result;
    }
}
