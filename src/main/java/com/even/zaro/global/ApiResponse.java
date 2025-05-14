package com.even.zaro.global;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;

    private ApiResponse(String code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }
//
//    public static <T> ApiResponse<T> success(T data, String status, String message) {
//        return new ApiResponse<>(data, status, message);
//    }

    //
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

//    public static <T> ApiResponse<T> fail(T data, String status, String message) {
//        return new ApiResponse<>(data, status, message);
//    }
}