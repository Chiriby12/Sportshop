package com.sportshop.auth.infraestructure.entry_points;

import com.sportshop.auth.application.dto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones.
 * Transforma cualquier error en una respuesta JSON clara para el frontend,
 * indicando exactamente qué campo falló y por qué.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Errores de validación de @Valid — muestra campo por campo qué falló
     * y qué valor se rechazó. Ej:
     * {
     *   "password": "La contraseña debe tener mayúscula... — ingresaste: abc123",
     *   "age": "Debes tener mínimo 18 años — ingresaste: 15"
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                campos.put(
                        error.getField(),
                        error.getDefaultMessage() + " — ingresaste: " + error.getRejectedValue()
                )
        );
        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, "Error de validación en los campos", campos),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Body JSON malformado o vacío
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonErrors(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST,
                        "El cuerpo de la solicitud es inválido — verifica que sea JSON válido", null),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Ruta no encontrada
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException ex) {
        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND,
                        "URL no encontrada — verifica que la ruta sea correcta: " + ex.getResourcePath(), null),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Acceso denegado (token sin permisos suficientes)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(
                buildError(HttpStatus.FORBIDDEN,
                        "No tienes permisos para realizar esta acción", null),
                HttpStatus.FORBIDDEN
        );
    }

    /**
     * Violación de constraint en BD (email o documento duplicado a nivel de BD)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST,
                        "Ya existe un registro con esos datos (email o documento duplicado)", null),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Errores de lógica de negocio lanzados desde el UseCase
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeErrors(RuntimeException ex) {
        String mensaje = ex.getMessage();
        HttpStatus status;

        if (mensaje != null && mensaje.contains("Contraseña incorrecta"))
            status = HttpStatus.UNAUTHORIZED;
        else if (mensaje != null && mensaje.contains("permiso"))
            status = HttpStatus.FORBIDDEN;
        else if (mensaje != null && (
                mensaje.contains("Ya existe") ||
                mensaje.contains("no coincide") ||
                mensaje.contains("vacío")
        ))
            status = HttpStatus.BAD_REQUEST;
        else
            status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(buildError(status, mensaje, null), status);
    }

    /**
     * Método HTTP no soportado en la ruta (ej: GET en endpoint que solo acepta POST)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(
                buildError(HttpStatus.METHOD_NOT_ALLOWED,
                        "Método " + ex.getMethod() + " no permitido en esta ruta — usa: " +
                        ex.getSupportedHttpMethods(), null),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    /**
     * Cualquier otro error inesperado
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralErrors(Exception ex) {
        return new ResponseEntity<>(
                buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error interno del servidor — " + ex.getMessage(), null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private <T> ApiResponse<T> buildError(HttpStatus status, String mensaje, T detalle) {
        return new ApiResponse<>(status.value(), mensaje, detalle, LocalDateTime.now().toString());
    }
}
