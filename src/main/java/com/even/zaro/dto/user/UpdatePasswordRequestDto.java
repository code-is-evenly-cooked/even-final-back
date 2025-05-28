package com.even.zaro.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequestDto {

    @Schema(description = "현재 비밀번호", example = "Old1234!")
    private String currentPassword;

    @Schema(description = "새 비밀번호", example = "New1234!")
    private String newPassword;
}
