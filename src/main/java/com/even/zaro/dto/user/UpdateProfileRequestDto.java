package com.even.zaro.dto.user;

import com.even.zaro.entity.Gender;
import com.even.zaro.entity.Mbti;
import com.even.zaro.global.validator.annotation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UpdateProfileRequestDto {

    @Schema(description = "생일", example = "1997-05-15", nullable = true)
    private LocalDate birthday;

    @Schema(description = "자취 시작일", example = "2024-01-01", nullable = true)
    private LocalDate liveAloneDate;

    @Schema(description = "성별", example = "MALE", nullable = true)
    @ValidEnum(enumClass = Gender.class)
    private String gender;

    @Schema(description = "MBTI", example = "INFP", nullable = true)
    @ValidEnum(enumClass = Mbti.class)
    private String mbti;
}
