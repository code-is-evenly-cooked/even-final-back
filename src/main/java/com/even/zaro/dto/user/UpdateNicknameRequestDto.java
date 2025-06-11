package com.even.zaro.dto.user;

import com.even.zaro.global.validator.annotation.ValidNickname;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateNicknameRequestDto {

    @Schema(description = "새 닉네임", example = "이브니짱")
    @NotBlank(message = "NEW_NICKNAME_REQUIRED")
    @ValidNickname
    private String newNickname;
}
