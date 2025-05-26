package com.even.zaro.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentionedUserDto {
    private Long id;

    private String nickname;
}
