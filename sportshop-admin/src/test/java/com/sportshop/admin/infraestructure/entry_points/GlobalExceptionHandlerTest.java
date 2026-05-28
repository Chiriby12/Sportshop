package com.sportshop.admin.infraestructure.entry_points;

import com.sportshop.admin.application.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("GlobalExceptionHandler (admin) - Tests del manejador de errores")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleRuntimeException: retorna 400 con el mensaje del error")
    void handleRuntimeException_ok() {
        RuntimeException ex = new RuntimeException("No existe un producto con id: 1");

        ResponseEntity<ApiResponse<Void>> response = handler.handleRuntimeException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("No existe un producto con id: 1", response.getBody().getMensaje());
    }

    @Test
    @DisplayName("handleValidation: retorna 400 con los mensajes de validación concatenados")
    void handleValidation_ok() throws Exception {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "obj");
        bindingResult.addError(new FieldError("obj", "name", "El nombre es obligatorio"));
        bindingResult.addError(new FieldError("obj", "price", "El precio es obligatorio"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody().getMensaje());
    }

    @Test
    @DisplayName("handleRuntimeException: mensaje null no lanza NullPointerException")
    void handleRuntimeException_mensajeNull() {
        RuntimeException ex = new RuntimeException((String) null);

        ResponseEntity<ApiResponse<Void>> response = handler.handleRuntimeException(ex);

        assertEquals(400, response.getStatusCode().value());
    }
}
