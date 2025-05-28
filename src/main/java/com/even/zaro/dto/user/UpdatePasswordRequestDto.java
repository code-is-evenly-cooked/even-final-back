package com.even.zaro.dto.user;

import lombok.Getter;

@Getter
public class UpdatePasswordRequestDto {

    private String currentPassword;

    private String newPassword;
}
