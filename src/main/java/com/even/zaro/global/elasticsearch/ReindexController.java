package com.even.zaro.global.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.even.zaro.global.elasticsearch.document.PostEsDocument;
import com.even.zaro.entity.Post;
import com.even.zaro.repository.PostRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/es")
@RequiredArgsConstructor
@Tag(name = "개발용입니다.", description = "개발용 API 입니다. 프론트에서 사용하지 않습니다. 메롱 ~:)")
public class ReindexController {

    private final PostRepository postRepository;
    private final ElasticsearchClient esClient;

    @Operation(summary = "ES 인덱스 재색인", description = "전체 게시글을 Elasticsearch에 재색인합니다. ES에 이미 존재하는 내용은 다시 들어가지 않습니다.")
    @PostMapping("/reindex")
    public ResponseEntity<String> reindex() throws IOException {
        List<Post> posts = postRepository.findAll();

        for (Post post : posts) {
            if (post.isDeleted() || post.isReported()) continue;

            esClient.index(i -> i
                    .index("posts")
                    .id(post.getId().toString())
                    .document(PostEsDocument.from(post))
            );
        }

        return ResponseEntity.ok("DB -> ES 복제 완료!");
    }
}
