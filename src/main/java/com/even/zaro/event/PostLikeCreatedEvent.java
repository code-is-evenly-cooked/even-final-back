package com.even.zaro.event;

import com.even.zaro.entity.PostLike;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostLikeCreatedEvent {
    private final PostLike postLike;
}