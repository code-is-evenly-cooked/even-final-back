package com.even.zaro.global.event.event;

import com.even.zaro.entity.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentCreatedEvent {
    private final Comment comment;
}