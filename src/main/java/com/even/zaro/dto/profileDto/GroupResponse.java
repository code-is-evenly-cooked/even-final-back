package com.even.zaro.dto.profileDto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GroupResponse {
    private long id;
    private String name;
}
