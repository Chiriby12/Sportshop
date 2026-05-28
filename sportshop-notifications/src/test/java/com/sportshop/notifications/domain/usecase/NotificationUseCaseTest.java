package com.sportshop.notifications.domain.usecase;

import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.domain.model.event.CatalogEvent;
import com.sportshop.notifications.domain.model.gateway.EmailSenderGateway;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock private NotificationGateway notificationGateway;
    @Mock private EmailSenderGateway emailSenderGateway;

    private NotificationUseCase useCase;
    private CatalogEvent event;
    private Notification notification;

    @BeforeEach
    void setUp() {
        useCase = new NotificationUseCase(notificationGateway, emailSenderGateway);
        useCase.setAdminEmail("admin@sportshop.com");

        event = new CatalogEvent();
        event.setType(CatalogEvent.EventType.PRODUCT_CREATED);
        event.setTitle("Balon de Futbol agregado");
        event.setMessage("El admin creo el producto: Balon de Futbol");
        event.setPerformedBy("12345678");
        event.setSourceService("catalog-service");
        event.setTimestamp(LocalDateTime.now());

        notification = new Notification();
        notification.setId(1L);
        notification.setType("PRODUCT_CREATED");
        notification.setTitle("Balon de Futbol agregado");
        notification.setMessage("El admin creo el producto: Balon de Futbol");
        notification.setPerformedBy("12345678");
        notification.setSourceService("catalog-service");
        notification.setStatus("RECEIVED");
        notification.setCreatedAt(LocalDateTime.now());
    }

    // ── receiveEvent ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("receiveEvent PRODUCT_CREATED: persiste y envia email al admin")
    void receiveEvent_productCreated_enviaEmail() {
        when(notificationGateway.save(any())).thenReturn(notification);
        doNothing().when(emailSenderGateway).sendEmail(anyString(), anyString(), anyString());

        Notification result = useCase.receiveEvent(event);

        assertNotNull(result);
        assertEquals("PRODUCT_CREATED", result.getType());
        // Verifica que se llamo al gateway de email
        verify(emailSenderGateway).sendEmail(
            eq("admin@sportshop.com"),
            contains("Nuevo producto"),
            anyString()
        );
    }

    @Test
    @DisplayName("receiveEvent PRODUCT_UPDATED: persiste pero NO envia email")
    void receiveEvent_productUpdated_noEnviaEmail() {
        event.setType(CatalogEvent.EventType.PRODUCT_UPDATED);
        when(notificationGateway.save(any())).thenReturn(notification);

        useCase.receiveEvent(event);

        // No debe llamar al email gateway para eventos distintos a PRODUCT_CREATED
        verify(emailSenderGateway, never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("receiveEvent CART_PURCHASED: persiste pero NO envia email")
    void receiveEvent_cartPurchased_noEnviaEmail() {
        event.setType(CatalogEvent.EventType.CART_PURCHASED);
        when(notificationGateway.save(any())).thenReturn(notification);

        useCase.receiveEvent(event);

        verify(emailSenderGateway, never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("receiveEvent: sourceService del evento se usa en la notificacion")
    void receiveEvent_sourceServiceDelEvento() {
        when(notificationGateway.save(any())).thenReturn(notification);
        doNothing().when(emailSenderGateway).sendEmail(any(), any(), any());

        useCase.receiveEvent(event);

        verify(notificationGateway).save(argThat(n -> "catalog-service".equals(n.getSourceService())));
    }

    @Test
    @DisplayName("receiveEvent: sourceService null usa 'catalog-service' por defecto")
    void receiveEvent_sourceServiceNull() {
        event.setSourceService(null);
        when(notificationGateway.save(any())).thenReturn(notification);
        doNothing().when(emailSenderGateway).sendEmail(any(), any(), any());

        useCase.receiveEvent(event);

        verify(notificationGateway).save(argThat(n -> "catalog-service".equals(n.getSourceService())));
    }

    @Test
    @DisplayName("receiveEvent: performedBy null usa 'system'")
    void receiveEvent_performedByNull() {
        event.setPerformedBy(null);
        when(notificationGateway.save(any())).thenReturn(notification);
        doNothing().when(emailSenderGateway).sendEmail(any(), any(), any());

        useCase.receiveEvent(event);

        verify(notificationGateway).save(argThat(n -> "system".equals(n.getPerformedBy())));
    }

    @Test
    @DisplayName("receiveEvent: title null usa el nombre del tipo")
    void receiveEvent_titleNull() {
        event.setTitle(null);
        when(notificationGateway.save(any())).thenReturn(notification);
        doNothing().when(emailSenderGateway).sendEmail(any(), any(), any());

        useCase.receiveEvent(event);

        verify(notificationGateway).save(argThat(n -> n.getTitle() != null && !n.getTitle().isBlank()));
    }

    @Test
    @DisplayName("receiveEvent: status siempre es RECEIVED al crear")
    void receiveEvent_statusReceived() {
        when(notificationGateway.save(any())).thenReturn(notification);
        doNothing().when(emailSenderGateway).sendEmail(any(), any(), any());

        useCase.receiveEvent(event);

        verify(notificationGateway).save(argThat(n -> "RECEIVED".equals(n.getStatus())));
    }

    @Test
    @DisplayName("receiveEvent: evento null lanza error")
    void receiveEvent_eventoNull() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.receiveEvent(null));
        assertEquals("El evento no puede ser nulo", ex.getMessage());
        verify(notificationGateway, never()).save(any());
        verify(emailSenderGateway, never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("receiveEvent: tipo null lanza error")
    void receiveEvent_tipoNull() {
        event.setType(null);
        assertThrows(RuntimeException.class, () -> useCase.receiveEvent(event));
        verify(emailSenderGateway, never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("receiveEvent: mensaje vacio lanza error")
    void receiveEvent_mensajeVacio() {
        event.setMessage("  ");
        assertThrows(RuntimeException.class, () -> useCase.receiveEvent(event));
    }

    @Test
    @DisplayName("receiveEvent: email falla silenciosamente (no rompe el flujo)")
    void receiveEvent_emailFallaNoRompeFlijo() {
        when(notificationGateway.save(any())).thenReturn(notification);
        doThrow(new RuntimeException("SMTP error")).when(emailSenderGateway).sendEmail(any(), any(), any());

        // No debe lanzar excepcion — el email falla silenciosamente
        assertDoesNotThrow(() -> useCase.receiveEvent(event));
        verify(notificationGateway).save(any());
    }

    // ── getAllNotifications ────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllNotifications: retorna lista completa")
    void getAll_ok() {
        when(notificationGateway.findAll()).thenReturn(List.of(notification));
        assertEquals(1, useCase.getAllNotifications().size());
    }

    @Test
    @DisplayName("getAllNotifications: lista vacia")
    void getAll_vacia() {
        when(notificationGateway.findAll()).thenReturn(Collections.emptyList());
        assertTrue(useCase.getAllNotifications().isEmpty());
    }

    // ── getNotificationById ───────────────────────────────────────────────────

    @Test
    @DisplayName("getNotificationById: retorna notificacion existente")
    void getById_ok() {
        when(notificationGateway.findById(1L)).thenReturn(Optional.of(notification));
        assertNotNull(useCase.getNotificationById(1L));
    }

    @Test
    @DisplayName("getNotificationById: ID negativo lanza error")
    void getById_negativo() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationById(-1L));
    }

    @Test
    @DisplayName("getNotificationById: ID cero lanza error")
    void getById_cero() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationById(0L));
    }

    @Test
    @DisplayName("getNotificationById: null lanza error")
    void getById_null() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationById(null));
    }

    @Test
    @DisplayName("getNotificationById: no existe lanza error con ID")
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
        assertEquals(1, useCase.getNotificationsByUser("12345678").size());
    }

    @Test
    @DisplayName("getNotificationsByUser: usuario vacio lanza error")
    void getByUser_vacio() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByUser("  "));
    }

    @Test
    @DisplayName("getNotificationsByUser: null lanza error")
    void getByUser_null() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByUser(null));
    }

    // ── getNotificationsByType ────────────────────────────────────────────────

    @Test
    @DisplayName("getNotificationsByType: retorna por tipo")
    void getByType_ok() {
        when(notificationGateway.findByType("PRODUCT_CREATED")).thenReturn(List.of(notification));
        assertEquals(1, useCase.getNotificationsByType("PRODUCT_CREATED").size());
    }

    @Test
    @DisplayName("getNotificationsByType: convierte a mayusculas")
    void getByType_mayusculas() {
        when(notificationGateway.findByType("PRODUCT_DELETED")).thenReturn(Collections.emptyList());
        useCase.getNotificationsByType("product_deleted");
        verify(notificationGateway).findByType("PRODUCT_DELETED");
    }

    @Test
    @DisplayName("getNotificationsByType: vacio lanza error")
    void getByType_vacio() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByType(""));
    }

    // ── getNotificationsByStatus ──────────────────────────────────────────────

    @Test
    @DisplayName("getNotificationsByStatus: RECEIVED funciona")
    void getByStatus_received() {
        when(notificationGateway.findByStatus("RECEIVED")).thenReturn(List.of(notification));
        assertEquals(1, useCase.getNotificationsByStatus("RECEIVED").size());
    }

    @Test
    @DisplayName("getNotificationsByStatus: READ funciona")
    void getByStatus_read() {
        when(notificationGateway.findByStatus("READ")).thenReturn(Collections.emptyList());
        assertTrue(useCase.getNotificationsByStatus("READ").isEmpty());
    }

    @Test
    @DisplayName("getNotificationsByStatus: estado invalido lanza error")
    void getByStatus_invalido() {
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> useCase.getNotificationsByStatus("PENDIENTE"));
        assertTrue(ex.getMessage().contains("RECEIVED o READ"));
    }

    @Test
    @DisplayName("getNotificationsByStatus: vacio lanza error")
    void getByStatus_vacio() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsByStatus(""));
    }

    // ── getNotificationsBySourceService ──────────────────────────────────────

    @Test
    @DisplayName("getNotificationsBySourceService: retorna por servicio")
    void getBySource_ok() {
        when(notificationGateway.findBySourceService("catalog-service")).thenReturn(List.of(notification));
        assertEquals(1, useCase.getNotificationsBySourceService("catalog-service").size());
    }

    @Test
    @DisplayName("getNotificationsBySourceService: vacio lanza error")
    void getBySource_vacio() {
        assertThrows(RuntimeException.class, () -> useCase.getNotificationsBySourceService("  "));
    }

    // ── markAsRead ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("markAsRead: marca como READ correctamente")
    void markAsRead_ok() {
        notification.setStatus("READ");
        when(notificationGateway.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationGateway.markAsRead(1L)).thenReturn(notification);
        assertEquals("READ", useCase.markAsRead(1L).getStatus());
    }

    @Test
    @DisplayName("markAsRead: ID negativo lanza error")
    void markAsRead_negativo() {
        assertThrows(RuntimeException.class, () -> useCase.markAsRead(-1L));
    }

    @Test
    @DisplayName("markAsRead: null lanza error")
    void markAsRead_null() {
        assertThrows(RuntimeException.class, () -> useCase.markAsRead(null));
    }

    @Test
    @DisplayName("markAsRead: no existe lanza error")
    void markAsRead_noExiste() {
        when(notificationGateway.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> useCase.markAsRead(99L));
        verify(notificationGateway, never()).markAsRead(any());
    }
}
