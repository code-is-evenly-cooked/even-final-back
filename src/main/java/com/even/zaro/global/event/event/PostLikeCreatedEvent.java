package com.even.zaro.global.event.event;

import com.even.zaro.entity.PostLike;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostLikeCreatedEvent {
    private final PostLike postLike;
}