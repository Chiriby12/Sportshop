package com.sportshop.notifications.domain.usecase;

import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.domain.model.event.CatalogEvent;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationUseCase - Tests")
class NotificationUseCaseTest {

    @Mock
    private NotificationGateway notificationGateway;

    @InjectMocks
    private NotificationUseCase useCase;

    private CatalogEvent event;
    private Notification notification;

    @BeforeEach
    void setUp() {
        event = new CatalogEvent();
        event.setType(CatalogEvent.EventType.PRODUCT_CREATED);
        event.setTitle("Nuevo producto creado");
        event.setMessage("El admin creó el producto: Balón de Fútbol");
        event.setPerformedBy("12345678");
        event.setTimestamp(LocalDateTime.now());

        notification = new Notification();
        notification.setId(1L);
        notification.setType("PRODUCT_CREATED");
        notification.setTitle("Nuevo producto creado");
        notification.setMessage("El admin creó el producto: Balón de Fútbol");
        notification.setPerformedBy("12345678");
        notification.setSourceService("catalog-service");
        notification.setStatus("RECEIVED");
        notification.setCreatedAt(LocalDateTime.now());
    }

    // ── receiveEvent ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("receiveEvent: persiste notificación correctamente")
    void receiveEvent_ok() {
        when(notificationGateway.save(any())).thenReturn(notification);
        Notification result = useCase.receiveEvent(event);
        assertNotNull(result);
        assertEquals("PRODUCT_CREATED", result.getType());
        assertEquals("RECEIVED", result.getStatus());
        verify(notificationGateway).save(any(Notification.class));
    }

    @Test
    @DisplayName("receiveEvent: asigna sourceService = catalog-service")
    void receiveEvent_sourceService() {
        when(notificationGateway.save(any())).thenReturn(notification);
        useCase.receiveEvent(event);
        verify(notificationGateway).save(argThat(n -> "catalog-service".equals(n.getSourceService())));
    }

    @Test
    @DisplayName("receiveEvent: asigna status = RECEIVED")
    void receiveEvent_status() {
        when(notificationGateway.save(any())).thenReturn(notification);
        useCase.receiveEvent(event);
        verify(notificationGateway).save(argThat(n -> "RECEIVED".equals(n.getStatus())));
    }

    @Test
    @DisplayName("receiveEvent: tipo se convierte a String del enum")
    void receiveEvent_tipoString() {
        when(notificationGateway.save(any())).thenReturn(notification);
        useCase.receiveEvent(event);
        verify(notificationGateway).save(argThat(n -> "PRODUCT_CREATED".equals(n.getType())));
    }

    @Test
    @DisplayName("receiveEvent: performedBy null usa 'system'")
    void receiveEvent_performedByNull() {
        event.setPerformedBy(null);
        when(notificationGateway.save(any())).thenReturn(notification);
        useCase.receiveEvent(event);
        verify(notificationGateway).save(argThat(n -> "system".equals(n.getPerformedBy())));
    }

    @Test
    @DisplayName("receiveEvent: title null usa nombre del tipo")
    void receiveEvent_titleNull() {
        event.setTitle(null);
        when(notificationGateway.save(any())).thenReturn(notification);
        useCase.receiveEvent(event);
        verify(notificationGateway).save(argThat(n -> n.getTitle() != null && !n.getTitle().isBlank()));
    }

    @Test
    @DisplayName("receiveEvent: evento null lanza error")
    void receiveEvent_eventoNull() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.receiveEvent(null));
        assertEquals("El evento no puede ser nulo", ex.getMessage());
        verify(notificationGateway, never()).save(any());
    }

    @Test
    @DisplayName("receiveEvent: tipo null lanza error")
    void receiveEvent_tipoNull() {
        event.setType(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.receiveEvent(event));
        assertEquals("El tipo del evento no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("receiveEvent: mensaje vacío lanza error")
    void receiveEvent_mensajeVacio() {
        event.setMessage("   ");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.receiveEvent(event));
        assertEquals("El mensaje del evento no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("receiveEvent: mensaje null lanza error")
    void receiveEvent_mensajeNull() {
        event.setMessage(null);
        assertThrows(RuntimeException.class, () -> useCase.receiveEvent(event));
    }

    // ── getAllNotifications ───────────────────────────────────────────────────

    @Test
    @DisplayName("getAllNotifications: retorna lista completa")
    void getAll_ok() {
        when(notificationGateway.findAll()).thenReturn(List.of(notification));
        List<Notification> result = useCase.getAllNotifications();
        assertEquals(1, result.size());
        verify(notificationGateway).findAll();
    }

    @Test
    @DisplayName("getAllNotifications: retorna lista vacía cuando no hay notificaciones")
    void getAll_vacia() {
        when(notificationGateway.findAll()).thenReturn(Collections.emptyList());
        List<Notification> result = useCase.getAllNotifications();
        assertTrue(result.isEmpty());
    }

    // ── getNotificationById ───────────────────────────────────────────────────

    @Test
    @DisplayName("getNotificationById: retorna notificación existente")
    void getById_ok() {
        when(notificationGateway.findById(1L)).thenReturn(Optional.of(notification));
        Notification result = useCase.getNotificationById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getNotificationById: ID negativo lanza error")
    void getById_idNegativo() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.getNotificationById(-1L));
        assertEquals("El ID debe ser un número positivo", ex.getMessage());
    }

    @Test
    @DisplayName("getNotificationById: ID cero lanza error")
    void getById_idCero() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationById(0L));
    }

    @Test
    @DisplayName("getNotificationById: ID null lanza error")
    void getById_idNull() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationById(null));
    }

    @Test
    @DisplayName("getNotificationById: no existe lanza error")
    void getById_noExiste() {
        when(notificationGateway.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.getNotificationById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ── getNotificationsByUser ────────────────────────────────────────────────

    @Test
    @DisplayName("getNotificationsByUser: retorna notificaciones del usuario")
    void getByUser_ok() {
        when(notificationGateway.findByPerformedBy("12345678")).thenReturn(List.of(notification));
        List<Notification> result = useCase.getNotificationsByUser("12345678");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getNotificationsByUser: usuario vacío lanza error")
    void getByUser_vacio() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByUser("  "));
    }

    @Test
    @DisplayName("getNotificationsByUser: usuario null lanza error")
    void getByUser_null() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByUser(null));
    }

    // ── getNotificationsByType ────────────────────────────────────────────────

    @Test
    @DisplayName("getNotificationsByType: retorna notificaciones del tipo")
    void getByType_ok() {
        when(notificationGateway.findByType("PRODUCT_CREATED")).thenReturn(List.of(notification));
        List<Notification> result = useCase.getNotificationsByType("PRODUCT_CREATED");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getNotificationsByType: convierte a mayúsculas")
    void getByType_mayus() {
        when(notificationGateway.findByType("PRODUCT_DELETED")).thenReturn(Collections.emptyList());
        useCase.getNotificationsByType("product_deleted");
        verify(notificationGateway).findByType("PRODUCT_DELETED");
    }

    @Test
    @DisplayName("getNotificationsByType: tipo vacío lanza error")
    void getByType_vacio() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByType(""));
    }

    @Test
    @DisplayName("getNotificationsByType: tipo null lanza error")
    void getByType_null() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByType(null));
    }

    // ── getNotificationsByStatus ──────────────────────────────────────────────

    @Test
    @DisplayName("getNotificationsByStatus: RECEIVED funciona correctamente")
    void getByStatus_received() {
        when(notificationGateway.findByStatus("RECEIVED")).thenReturn(List.of(notification));
        List<Notification> result = useCase.getNotificationsByStatus("RECEIVED");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getNotificationsByStatus: READ funciona correctamente")
    void getByStatus_read() {
        when(notificationGateway.findByStatus("READ")).thenReturn(Collections.emptyList());
        assertTrue(useCase.getNotificationsByStatus("READ").isEmpty());
    }

    @Test
    @DisplayName("getNotificationsByStatus: estado inválido lanza error")
    void getByStatus_invalido() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.getNotificationsByStatus("PENDIENTE"));
        assertTrue(ex.getMessage().contains("RECEIVED o READ"));
    }

    @Test
    @DisplayName("getNotificationsByStatus: estado vacío lanza error")
    void getByStatus_vacio() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByStatus(""));
    }

    @Test
    @DisplayName("getNotificationsByStatus: estado null lanza error")
    void getByStatus_null() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByStatus(null));
    }

    // ── getNotificationsBySourceService ──────────────────────────────────────

    @Test
    @DisplayName("getNotificationsBySourceService: retorna notificaciones del servicio")
    void getBySource_ok() {
        when(notificationGateway.findBySourceService("catalog-service")).thenReturn(List.of(notification));
        List<Notification> result = useCase.getNotificationsBySourceService("catalog-service");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getNotificationsBySourceService: servicio vacío lanza error")
    void getBySource_vacio() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsBySourceService("  "));
    }

    // ── markAsRead ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("markAsRead: marca correctamente como leída")
    void markAsRead_ok() {
        notification.setStatus("READ");
        when(notificationGateway.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationGateway.markAsRead(1L)).thenReturn(notification);
        Notification result = useCase.markAsRead(1L);
        assertEquals("READ", result.getStatus());
        verify(notificationGateway).markAsRead(1L);
    }

    @Test
    @DisplayName("markAsRead: ID negativo lanza error")
    void markAsRead_idNegativo() {
        assertThrows(RuntimeException.class, () -> useCase.markAsRead(-5L));
    }

    @Test
    @DisplayName("markAsRead: ID null lanza error")
    void markAsRead_idNull() {
        assertThrows(RuntimeException.class, () -> useCase.markAsRead(null));
    }

    @Test
    @DisplayName("markAsRead: notificación no existe lanza error")
    void markAsRead_noExiste() {
        when(notificationGateway.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> useCase.markAsRead(99L));
        verify(notificationGateway, never()).markAsRead(any());
    }
}
