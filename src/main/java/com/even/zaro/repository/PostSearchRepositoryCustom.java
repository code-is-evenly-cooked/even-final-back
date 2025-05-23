package com.even.zaro.repository;

import com.even.zaro.dto.post.PostSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSearchRepositoryCustom {
    Page<PostSearchDto> searchPosts(String category, String keyword, Pageable pageable);
}
