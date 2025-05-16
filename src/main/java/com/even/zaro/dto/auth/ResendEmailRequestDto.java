package com.even.zaro.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResendEmailRequestDto {

    @Schema(description = "이메일", example = "test@even.com", required = true)
    @NotBlank
    private String email;
}
