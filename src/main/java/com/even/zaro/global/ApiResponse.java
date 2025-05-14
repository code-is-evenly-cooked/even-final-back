package com.even.zaro.global;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {

    private T data;
    private String status;
    private String message;

    private ApiResponse(T data, String success, String message) {
        this.data = data;
        this.status = success;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, "SUCCESS", message);
    }
//
//    public static <T> ApiResponse<T> success(T data, String status, String message) {
//        return new ApiResponse<>(data, status, message);
//    }

    //
    public static <T> ApiResponse<T> fail(String status, String message) {
        return new ApiResponse<>(null, status, message);
    }

//    public static <T> ApiResponse<T> fail(T data, String status, String message) {
//        return new ApiResponse<>(data, status, message);
//    }
}