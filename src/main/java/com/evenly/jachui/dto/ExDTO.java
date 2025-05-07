package com.evenly.jachui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ExDTO {

    @Schema(name = "userId", example = "1")
    private int id;

    @Schema(name = "username", example = "동훈")
    private String username;

}
