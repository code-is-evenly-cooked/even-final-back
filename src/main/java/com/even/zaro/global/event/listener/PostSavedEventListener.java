package com.even.zaro.global.event.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.even.zaro.global.elasticsearch.document.PostEsDocument;
import com.even.zaro.entity.Post;
import com.even.zaro.global.event.event.PostSavedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PostSavedEventListener {
    private final ElasticsearchClient elasticsearchClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PostSavedEvent event) throws IOException {
        Post post = event.getPost();
        elasticsearchClient.index(i -> i
                .index("posts")
                .id(post.getId().toString())
                .document(PostEsDocument.from(post)));
    }
}
