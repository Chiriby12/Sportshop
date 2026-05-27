package com.sportshop.notifications.infraestructure.mapper;

import com.sportshop.notifications.application.dto.NotificationResponseDTO;
import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.infraestructure.driver_adapters.jpa_repository.NotificationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NotificationMapper - Tests")
class NotificationMapperTest {

    private NotificationMapper mapper;
    private Notification notification;
    private NotificationData notificationData;

    @BeforeEach
    void setUp() {
        mapper = new NotificationMapper();

        notification = new Notification(1L, "PRODUCT_CREATED", "Producto creado",
                "El admin creó un producto", "12345678", "catalog-service",
                "RECEIVED", LocalDateTime.now());

        notificationData = NotificationData.builder()
                .id(1L).type("PRODUCT_CREATED").title("Producto creado")
                .message("El admin creó un producto").performedBy("12345678")
                .sourceService("catalog-service").status("RECEIVED")
                .createdAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("toDomain: convierte NotificationData a Notification correctamente")
    void toDomain_ok() {
        Notification result = mapper.toDomain(notificationData);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PRODUCT_CREATED", result.getType());
        assertEquals("catalog-service", result.getSourceService());
        assertEquals("RECEIVED", result.getStatus());
    }

    @Test
    @DisplayName("toDomain: null retorna null")
    void toDomain_null() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("toData: convierte Notification a NotificationData correctamente")
    void toData_ok() {
        NotificationData result = mapper.toData(notification);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PRODUCT_CREATED", result.getType());
        assertEquals("12345678", result.getPerformedBy());
    }

    @Test
    @DisplayName("toData: null retorna null")
    void toData_null() {
        assertNull(mapper.toData(null));
    }

    @Test
    @DisplayName("toResponseDTO: convierte Notification a DTO correctamente")
    void toResponseDTO_ok() {
        NotificationResponseDTO result = mapper.toResponseDTO(notification);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PRODUCT_CREATED", result.getType());
        assertEquals("El admin creó un producto", result.getMessage());
        assertEquals("RECEIVED", result.getStatus());
    }

    @Test
    @DisplayName("toResponseDTO: null retorna null")
    void toResponseDTO_null() {
        assertNull(mapper.toResponseDTO(null));
    }

    @Test
    @DisplayName("toDomain → toData: round-trip conserva datos")
    void roundTrip() {
        NotificationData data = mapper.toData(notification);
        Notification result = mapper.toDomain(data);
        assertEquals(notification.getId(), result.getId());
        assertEquals(notification.getType(), result.getType());
        assertEquals(notification.getStatus(), result.getStatus());
    }
}
