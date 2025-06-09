package com.even.zaro.global.event.event;

import com.even.zaro.entity.Follow;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowCreatedEvent {
    private final Follow follow;
}
