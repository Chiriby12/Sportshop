package com.sportshop.notifications.infraestructure.driver_adapters;

import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.infraestructure.driver_adapters.jpa_repository.NotificationData;
import com.sportshop.notifications.infraestructure.driver_adapters.jpa_repository.NotificationGatewayImpl;
import com.sportshop.notifications.infraestructure.driver_adapters.jpa_repository.NotificationJpaRepository;
import com.sportshop.notifications.infraestructure.mapper.NotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationGatewayImpl - Tests")
class NotificationGatewayImplTest {

    @Mock private NotificationJpaRepository repository;
    @Mock private NotificationMapper mapper;
    @InjectMocks private NotificationGatewayImpl gateway;

    private Notification notification;
    private NotificationData notificationData;

    @BeforeEach
    void setUp() {
        notification = new Notification(1L, "PRODUCT_CREATED", "Producto creado",
                "El admin creó un producto", "12345678", "catalog-service",
                "RECEIVED", LocalDateTime.now());
        notificationData = NotificationData.builder()
                .id(1L).type("PRODUCT_CREATED").title("Producto creado")
                .message("El admin creó un producto").performedBy("12345678")
                .sourceService("catalog-service").status("RECEIVED")
                .createdAt(LocalDateTime.now()).build();
    }

    @Test @DisplayName("save: persiste y retorna dominio")
    void save_ok() {
        when(mapper.toData(notification)).thenReturn(notificationData);
        when(repository.save(notificationData)).thenReturn(notificationData);
        when(mapper.toDomain(notificationData)).thenReturn(notification);
        Notification result = gateway.save(notification);
        assertNotNull(result);
        verify(repository).save(notificationData);
    }

    @Test @DisplayName("findAll: retorna lista mapeada")
    void findAll_ok() {
        when(repository.findAll()).thenReturn(List.of(notificationData));
        when(mapper.toDomain(notificationData)).thenReturn(notification);
        assertEquals(1, gateway.findAll().size());
    }

    @Test @DisplayName("findById: retorna Optional con dominio")
    void findById_ok() {
        when(repository.findById(1L)).thenReturn(Optional.of(notificationData));
        when(mapper.toDomain(notificationData)).thenReturn(notification);
        assertTrue(gateway.findById(1L).isPresent());
    }

    @Test @DisplayName("findById: no existe retorna Optional vacío")
    void findById_noExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(gateway.findById(99L).isEmpty());
    }

    @Test @DisplayName("findByPerformedBy: filtra correctamente")
    void findByPerformedBy_ok() {
        when(repository.findByPerformedBy("12345678")).thenReturn(List.of(notificationData));
        when(mapper.toDomain(notificationData)).thenReturn(notification);
        assertEquals(1, gateway.findByPerformedBy("12345678").size());
    }

    @Test @DisplayName("findByType: filtra por tipo")
    void findByType_ok() {
        when(repository.findByType("PRODUCT_CREATED")).thenReturn(List.of(notificationData));
        when(mapper.toDomain(notificationData)).thenReturn(notification);
        assertEquals(1, gateway.findByType("PRODUCT_CREATED").size());
    }

    @Test @DisplayName("findByStatus: filtra por estado")
    void findByStatus_ok() {
        when(repository.findByStatus("RECEIVED")).thenReturn(List.of(notificationData));
        when(mapper.toDomain(notificationData)).thenReturn(notification);
        assertEquals(1, gateway.findByStatus("RECEIVED").size());
    }

    @Test @DisplayName("findBySourceService: filtra por servicio")
    void findBySourceService_ok() {
        when(repository.findBySourceService("catalog-service")).thenReturn(List.of(notificationData));
        when(mapper.toDomain(notificationData)).thenReturn(notification);
        assertEquals(1, gateway.findBySourceService("catalog-service").size());
    }

    @Test @DisplayName("markAsRead: cambia estado a READ")
    void markAsRead_ok() {
        when(repository.findById(1L)).thenReturn(Optional.of(notificationData));
        notificationData.setStatus("READ");
        when(repository.save(notificationData)).thenReturn(notificationData);
        when(mapper.toDomain(notificationData)).thenReturn(notification);
        Notification result = gateway.markAsRead(1L);
        assertNotNull(result);
        verify(repository).save(notificationData);
    }

    @Test @DisplayName("markAsRead: no existe lanza error")
    void markAsRead_noExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> gateway.markAsRead(99L));
    }
}
