package com.even.zaro.dto.post;

import com.even.zaro.entity.ReportReasonType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ReportRequestDTO (

    @Schema(description = "신고 사유 ENUM", example = "SPAM")
    ReportReasonType reasonType,

    @Schema(description = "기타 사유 선택시에만 입력 ETC", example = "전혀 연관 없는 글과 욕설으로 타인의 기분을 망칩니다.")
    String reasonText
){}
