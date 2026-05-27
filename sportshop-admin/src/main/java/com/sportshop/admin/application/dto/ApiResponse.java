package com.sportshop.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String mensaje;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> ok(String mensaje, T data) {
        return new ApiResponse<>(200, mensaje, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> created(String mensaje, T data) {
        return new ApiResponse<>(201, mensaje, data, LocalDateTime.now());
    }
}
