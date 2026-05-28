package com.sportshop.notifications.infraestructure.entry_points;

import com.sportshop.notifications.application.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("GlobalExceptionHandler (notifications) - Tests del manejador de errores")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleRuntime: 'No existe' retorna 404")
    void handleRuntime_noExiste_404() {
        RuntimeException ex = new RuntimeException("No existe una notificación con id: 1");

        ResponseEntity<ApiResponse<Void>> response = handler.handleRuntime(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("No existe una notificación con id: 1", response.getBody().getMensaje());
    }

    @Test
    @DisplayName("handleRuntime: otro error retorna 400")
    void handleRuntime_otroError_400() {
        RuntimeException ex = new RuntimeException("El tipo no puede estar vacío");

        ResponseEntity<ApiResponse<Void>> response = handler.handleRuntime(ex);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleRuntime: mensaje null retorna 400 sin NullPointerException")
    void handleRuntime_mensajeNull() {
        RuntimeException ex = new RuntimeException((String) null);

        ResponseEntity<ApiResponse<Void>> response = handler.handleRuntime(ex);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleGeneral: Exception genérica retorna 500")
    void handleGeneral_500() {
        Exception ex = new Exception("Error inesperado en el servidor");

        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneral(ex);

        assertEquals(500, response.getStatusCode().value());
    }
}
