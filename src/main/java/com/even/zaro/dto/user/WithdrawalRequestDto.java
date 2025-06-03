package com.even.zaro.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 탈퇴 요청 DTO")
public class WithdrawalRequestDto {

    @Schema(description = "(선택) 탈퇴 사유", example = "자취 정보가 부족했어요")
    private String reason;
}
