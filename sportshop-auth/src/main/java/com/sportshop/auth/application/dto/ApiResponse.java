package com.sportshop.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String mensaje;
    private T data;
    private String timestamp;

    public static <T> ApiResponse<T> ok(String mensaje, T data) {
        return new ApiResponse<>(200, mensaje, data,
                java.time.LocalDateTime.now().toString());
    }

    public static <T> ApiResponse<T> created(String mensaje, T data) {
        return new ApiResponse<>(201, mensaje, data,
                java.time.LocalDateTime.now().toString());
    }
}
