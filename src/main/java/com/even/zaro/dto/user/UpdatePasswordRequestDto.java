package com.even.zaro.dto.user;

import com.even.zaro.global.validator.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequestDto {

    @Schema(description = "현재 비밀번호", example = "Old1234!")
    @NotBlank(message = "CURRENT_PASSWORD_REQUIRED")
    private String currentPassword;

    @Schema(description = "새 비밀번호", example = "New1234!")
    @NotBlank(message = "PASSWORD_REQUIRED")
    @ValidPassword
    private String newPassword;
}
