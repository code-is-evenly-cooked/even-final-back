package com.even.zaro.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.post.PostSearchDto;
import com.even.zaro.global.elasticsearch.document.PostEsDocument;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.post.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class PostEsSearchService {
    private final ElasticsearchClient elasticsearchClient;

    public PageResponse<PostSearchDto> searchWithPage(String category, String tag, String keyword, Pageable pageable) throws IOException {
        if (keyword == null || keyword.isBlank()) {
            throw new PostException(ErrorCode.SEARCH_KEYWORD_REQUIRED);
        }

        String[] terms = keyword.trim().split("\\s+");

        Query keywordQuery = BoolQuery.of(b -> {
            for (String term : terms) {
                b.must(m -> m.multiMatch(mm -> mm
                        .fields("title", "content")
                        .query(term)
                        .fuzziness("AUTO")
                ));
            }
            return b;
        })._toQuery();

        BoolQuery.Builder bool = new BoolQuery.Builder()
                .must(keywordQuery);

        if (category !=null && !category.isBlank()) {
            Query categoryFilter = TermQuery.of(t -> t
                    .field("category")
                    .value(category)
            )._toQuery();
            bool.filter(categoryFilter);
        }

        if (tag !=null && !tag.isBlank()) {
            Query tagFilter = TermQuery.of(t -> t
                    .field("tag")
                    .value(tag)
            )._toQuery();
            bool.filter(tagFilter);
        }

        SearchResponse<PostEsDocument> response = elasticsearchClient.search(s -> s
                .index("posts")
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize())
                .query(q -> q.bool(bool.build())),
                PostEsDocument.class);

        if (response.hits().hits().isEmpty()) {
            throw new PostException(ErrorCode.SEARCH_POST_NOT_FOUND);
        }

        List<PostSearchDto> content = response.hits().hits().stream()
                .map(hit -> {
                    PostEsDocument doc = hit.source();
                    if (doc == null) return null;
                    return new PostSearchDto(
                            doc.getId(),
                            doc.getTitle(),
                            doc.getContent(),
                            doc.getThumbnailImage(),
                            doc.getCategory(),
                            doc.getTag(),
                            doc.getLikeCount(),
                            doc.getCommentCount(),
                            OffsetDateTime.parse(doc.getCreatedAt())
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        return new PageResponse<>(new PageImpl<>(content, pageable, total));
    }
}
