package com.even.zaro.dto.post;

import com.even.zaro.entity.PostReport;
import com.even.zaro.entity.ReportReasonType;

public record ReportResponseDto (
        ReportReasonType reasonType,
        String description,
        String reasonText
){
    public static ReportResponseDto from(PostReport report) {
        return new ReportResponseDto(
                report.getReasonType(),
                report.getReasonType().getDescription(),
                report.getReasonText()
        );
    }
}

