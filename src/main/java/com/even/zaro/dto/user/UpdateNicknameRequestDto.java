package com.even.zaro.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateNicknameRequestDto {
    private String newNickname;
}
