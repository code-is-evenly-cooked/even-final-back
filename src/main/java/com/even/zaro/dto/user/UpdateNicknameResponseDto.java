package com.even.zaro.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpdateNicknameResponseDto {

    @Schema(description = "변경된 닉네임", example = "새닉네임")
    private String nickname;

    @Schema(description = "다음 닉네임 변경 가능 일시", example = "2025-06-13T12:00:00")
    private LocalDateTime nextAvailableChangeDate;
}
