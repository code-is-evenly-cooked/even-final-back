package com.even.zaro.event;

import com.even.zaro.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostSavedEvent {

    private final Post post;

}
