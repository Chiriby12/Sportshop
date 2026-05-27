package com.sportshop.notifications.infraestructure.entry_points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sportshop.notifications.application.config.SecurityConfig;
import com.sportshop.notifications.application.config.UseCaseConfig;
import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.domain.model.event.CatalogEvent;
import com.sportshop.notifications.domain.usecase.NotificationUseCase;
import com.sportshop.notifications.infraestructure.mapper.NotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import({SecurityConfig.class, UseCaseConfig.class, NotificationMapper.class})
@DisplayName("NotificationController - Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationUseCase notificationUseCase;

    @MockitoBean
    private NotificationMapper notificationMapper;

    @MockitoBean
    private JwtFilter jwtFilter;

    private ObjectMapper objectMapper;
    private Notification notification;
    private CatalogEvent event;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        notification = new Notification(1L, "PRODUCT_CREATED", "Producto creado",
                "El admin creó un producto", "12345678", "catalog-service",
                "RECEIVED", LocalDateTime.now());

        event = new CatalogEvent();
        event.setType(CatalogEvent.EventType.PRODUCT_CREATED);
        event.setTitle("Producto creado");
        event.setMessage("El admin creó el producto: Balón");
        event.setPerformedBy("12345678");
        event.setTimestamp(LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /receive: persiste notificación - sin token (público)")
    void receive_ok() throws Exception {
        when(notificationUseCase.receiveEvent(any())).thenReturn(notification);
        when(notificationMapper.toResponseDTO(any())).thenCallRealMethod();

        mockMvc.perform(post("/api/sportshop/notifications/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /: listar todas - requiere ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getAll_ok() throws Exception {
        when(notificationUseCase.getAllNotifications()).thenReturn(List.of(notification));
        when(notificationMapper.toResponseDTO(any())).thenCallRealMethod();

        mockMvc.perform(get("/api/sportshop/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /: sin token retorna 401")
    void getAll_sinToken() throws Exception {
        mockMvc.perform(get("/api/sportshop/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /{id}: obtener por ID - USER autenticado")
    @WithMockUser(roles = "USER")
    void getById_ok() throws Exception {
        when(notificationUseCase.getNotificationById(1L)).thenReturn(notification);
        when(notificationMapper.toResponseDTO(any())).thenCallRealMethod();

        mockMvc.perform(get("/api/sportshop/notifications/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/{performedBy}: notificaciones de un usuario - USER autenticado")
    @WithMockUser(roles = "USER")
    void getByUser_ok() throws Exception {
        when(notificationUseCase.getNotificationsByUser("12345678")).thenReturn(List.of(notification));
        when(notificationMapper.toResponseDTO(any())).thenCallRealMethod();

        mockMvc.perform(get("/api/sportshop/notifications/user/12345678"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /type/{type}: filtrar por tipo - ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getByType_ok() throws Exception {
        when(notificationUseCase.getNotificationsByType("PRODUCT_CREATED")).thenReturn(List.of(notification));
        when(notificationMapper.toResponseDTO(any())).thenCallRealMethod();

        mockMvc.perform(get("/api/sportshop/notifications/type/PRODUCT_CREATED"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /status/{status}: filtrar por estado - ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getByStatus_ok() throws Exception {
        when(notificationUseCase.getNotificationsByStatus("RECEIVED")).thenReturn(List.of(notification));
        when(notificationMapper.toResponseDTO(any())).thenCallRealMethod();

        mockMvc.perform(get("/api/sportshop/notifications/status/RECEIVED"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /{id}/read: marcar como leída - USER autenticado")
    @WithMockUser(roles = "USER")
    void markAsRead_ok() throws Exception {
        notification.setStatus("READ");
        when(notificationUseCase.markAsRead(1L)).thenReturn(notification);
        when(notificationMapper.toResponseDTO(any())).thenCallRealMethod();

        mockMvc.perform(patch("/api/sportshop/notifications/1/read"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /type/{type}: USER sin ADMIN retorna 403")
    @WithMockUser(roles = "USER")
    void getByType_sinAdmin() throws Exception {
        mockMvc.perform(get("/api/sportshop/notifications/type/PRODUCT_CREATED"))
                .andExpect(status().isForbidden());
    }
}
