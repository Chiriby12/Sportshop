package com.sportshop.notifications.infraestructure.entry_points;

import com.sportshop.notifications.application.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException e) {
        int status = e.getMessage() != null && e.getMessage().contains("No existe") ? 404 : 400;
        return ResponseEntity.status(status)
                .body(ApiResponse.error(status, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception e) {
        return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "Error interno del servidor: " + e.getMessage()));
    }
}
