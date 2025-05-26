package com.even.zaro.entity;

public enum ReportReasonType {
    SPAM("스팸, 광고, 도배와 같은 내용이에요."),
    INSULT("욕설 또는 공격적인 표현이 있어요."),
    INAPPROPRIATE("부적절하거나 불쾌한 표현이 있어요."),
    FRAUD("사기, 허위 사실 등이 포함되어 있어요."),
    SEXUAL("선정적이거나 성적인 표현이 있어요."),
    VIOLENCE("폭력적이거나 위협적인 내용이 있어요."),
    ILLEGAL("불법적인 활동이나 정보가 포함되어 있어요."),
    ETC("기타 사유입니다. (직접 작성해 주세요)");

    private final String description;

    ReportReasonType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
