package com.even.zaro.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.post.PostSearchDto;
import com.even.zaro.elasticsearch.document.PostEsDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class PostEsSearchService {
    private final ElasticsearchClient elasticsearchClient;

    public PageResponse<PostSearchDto> searchWithPage(String category, String keyword, Pageable pageable) throws IOException {
        Query keywordQuery = MatchQuery.of(m -> m
                .field("title")
                .query(keyword)
        )._toQuery();

        BoolQuery.Builder bool = new BoolQuery.Builder()
                .must(keywordQuery);

        if (category !=null && !category.isBlank()) {
            Query categoryFilter = TermQuery.of(t -> t
                    .field("category")
                    .value(category)
            )._toQuery();
            bool.filter(categoryFilter);
        }

        SearchResponse<PostEsDocument> response = elasticsearchClient.search(s -> s
                .index("posts")
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize())
                .query(q -> q.bool(bool.build())),
                PostEsDocument.class);

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
                            doc.getCreatedAt()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        return new PageResponse<>(new PageImpl<>(content, pageable, total));
    }
}
