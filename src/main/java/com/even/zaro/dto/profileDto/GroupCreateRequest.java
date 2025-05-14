package com.even.zaro.dto.profileDto;

import lombok.Data;

@Data
public class GroupCreateRequest {
    private long userId;
    // Group 이름
    private String name;
}
