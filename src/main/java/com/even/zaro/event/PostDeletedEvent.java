package com.even.zaro.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostDeletedEvent {
    private final Long postId;
}
