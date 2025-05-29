package com.even.zaro.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpdateNicknameResponseDto {

    private String nickname;
    private LocalDateTime nextAvailableChangeDate;
}
