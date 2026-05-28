package com.sportshop.notifications.infraestructure.entry_points;

import com.sportshop.notifications.application.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Error interno";
        int status = msg.startsWith("No existe") ? 404 : 400;
        log.warn("RuntimeException en notifications: {}", msg);
        return ResponseEntity.status(status).body(new ApiResponse<>(
                status, msg, null, LocalDateTime.now()
        ));
    }

    /**
     * Captura errores de deserialización de Jackson.
     * Sin este handler, cuando llega un tipo de evento desconocido
     * el error se traga silenciosamente y la notificación nunca se guarda.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleDeserializacion(HttpMessageNotReadableException ex) {
        log.error("Error deserializando evento entrante — revisa que el tipo del evento exista en CatalogEvent.EventType. Detalle: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Error al leer el cuerpo de la petición: " + ex.getMessage(),
                null,
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validación fallida en notifications: {}", errors);
        return ResponseEntity.badRequest().body(new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación: " + errors,
                null,
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Error inesperado en notifications: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                null,
                LocalDateTime.now()
        ));
    }
}