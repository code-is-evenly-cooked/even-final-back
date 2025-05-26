package com.even.zaro.dto.post;

import com.even.zaro.entity.ReportReasonType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {

    @Schema(description = "신고 사유(Type)", example = "ETC")
    private ReportReasonType reasonType;

    @Schema(description = "기타 사유 선택시에만 입력.\n" +
            "※ reasonType이 ETC일 때만 필수. 그 외에는 null 또는 빈 문자열 \"\" 가능.",
            example = "전혀 연관 없는 글과 욕설로 타인의 기분을 불편하게 합니다.")
    private String reasonText;
}