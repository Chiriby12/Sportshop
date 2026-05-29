package com.sportshop.auth.infraestructure.entry_points;

import com.sportshop.auth.application.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock; // 1. Agregamos el import de Mockito

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleValidationErrors: retorna 400 con mapa de campos")
    void handleValidationErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "obj");
        bindingResult.addError(new FieldError("obj", "email", "bad@",
                false, null, null, "Email inválido"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponse<Map<String, String>>> response = handler.handleValidationErrors(ex);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleJsonErrors: retorna 400")
    void handleJsonErrors() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("bad json");
        ResponseEntity<?> response = handler.handleJsonErrors(ex);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleAccessDenied: retorna 403")
    void handleAccessDenied() {
        ResponseEntity<?> response = handler.handleAccessDenied(new AccessDeniedException("denied"));
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleDataIntegrity: retorna 400")
    void handleDataIntegrity() {
        ResponseEntity<?> response = handler.handleDataIntegrity(new DataIntegrityViolationException("dup"));
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleMethodNotSupported: retorna 405")
    void handleMethodNotSupported() {
        ResponseEntity<?> response = handler.handleMethodNotSupported(
                new HttpRequestMethodNotSupportedException("PATCH"));
        assertEquals(405, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleRuntimeErrors: contraseña incorrecta retorna 401")
    void handleRuntime_passwordIncorrecta() {
        ResponseEntity<?> response = handler.handleRuntimeErrors(new RuntimeException("Contraseña incorrecta"));
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleRuntimeErrors: permiso retorna 403")
    void handleRuntime_permiso() {
        ResponseEntity<?> response = handler.handleRuntimeErrors(new RuntimeException("No tienes permiso"));
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleRuntimeErrors: Ya existe retorna 400")
    void handleRuntime_yaExiste() {
        ResponseEntity<?> response = handler.handleRuntimeErrors(new RuntimeException("Ya existe un usuario"));
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleRuntimeErrors: otro error retorna 404")
    void handleRuntime_otroError() {
        ResponseEntity<?> response = handler.handleRuntimeErrors(new RuntimeException("No existe el recurso"));
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleGeneralErrors: retorna 500")
    void handleGeneralErrors() {
        ResponseEntity<?> response = handler.handleGeneralErrors(new Exception("error general"));
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleNoResource: retorna 404")
    void handleNoResource() {
        // 2. Solución: Simulamos la excepción con mock() para no depender de su constructor
        NoResourceFoundException ex = mock(NoResourceFoundException.class);

        ResponseEntity<?> response = handler.handleNoResource(ex);
        assertEquals(404, response.getStatusCode().value());
    }
}