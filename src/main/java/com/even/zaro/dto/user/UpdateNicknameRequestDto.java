package com.even.zaro.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateNicknameRequestDto {

    @Schema(description = "새 닉네임", example = "이브니짱")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,12}$")
    private String newNickname;
}
