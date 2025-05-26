package com.even.zaro.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.even.zaro.event.PostDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PostDeletedEventListener {

    private final ElasticsearchClient elasticsearchClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PostDeletedEvent event) throws IOException {
        elasticsearchClient.delete(d -> d
                .index("posts")
                .id(event.getPostId().toString()));
    }
}
