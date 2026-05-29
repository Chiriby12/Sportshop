package com.sportshop.notifications.application.config;

import com.sportshop.notifications.domain.usecase.NotificationUseCase;
import com.sportshop.notifications.infraestructure.entry_points.GlobalExceptionHandler;
import com.sportshop.notifications.infraestructure.entry_points.JwtFilter;
import com.sportshop.notifications.infraestructure.entry_points.NotificationController;
import com.sportshop.notifications.infraestructure.mapper.NotificationMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationController.class)
@Import({SecurityConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = "jwt.secret=3f8a2b1c9d7e4f6a0b5c8d2e1f7a3b4c9d6e5f8a2b1c0d7e4f3a6b5c8d9e2f1a")
@DisplayName("SecurityConfig — Tests de seguridad de notificaciones")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private NotificationUseCase notificationUseCase;
    @MockitoBean private NotificationMapper notificationMapper;

    @Test
    @DisplayName("Swagger UI es accesible sin token")
    void swagger_publico() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertNotEquals(
                            401, status, "Swagger no deberia requerir autenticacion");
                });
    }

    @Test
    @DisplayName("GET /api-docs es accesible sin token")
    void apiDocs_publico() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertNotEquals(
                            401, status, "/api-docs no deberia requerir autenticacion");
                });
    }

    @Test
    @DisplayName("GET /api/sportshop/notifications sin token retorna 401")
    void notifications_sinToken_401() throws Exception {
        mockMvc.perform(get("/api/sportshop/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/sportshop/notifications/user/123 sin token retorna 401")
    void notificationsByUser_sinToken_401() throws Exception {
        mockMvc.perform(get("/api/sportshop/notifications/user/123"))
                .andExpect(status().isUnauthorized());
    }
}