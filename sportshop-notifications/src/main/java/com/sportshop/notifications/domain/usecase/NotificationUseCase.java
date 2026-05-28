package com.sportshop.notifications.domain.usecase;

import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.domain.model.event.CatalogEvent;
import com.sportshop.notifications.domain.model.gateway.EmailSenderGateway;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso del dominio de Notificaciones.
 * Arquitectura Hexagonal: NUCLEO del hexagono.
 * Sin dependencias de Spring ni JPA — solo interfaces (puertos).
 */
@RequiredArgsConstructor
public class NotificationUseCase {

    private final NotificationGateway notificationGateway;
    private final EmailSenderGateway emailSenderGateway;

    // Email del admin — inyectado desde UseCaseConfig
    private String adminEmail = "admin@sportshop.com";

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    /**
     * Recibe un evento de otro microservicio, lo persiste y,
     * si es PRODUCT_CREATED, envia email al admin.
     */
    public Notification receiveEvent(CatalogEvent event) {
        if (event == null)
            throw new RuntimeException("El evento no puede ser nulo");
        if (event.getType() == null)
            throw new RuntimeException("El tipo del evento no puede ser nulo");
        if (event.getMessage() == null || event.getMessage().trim().isEmpty())
            throw new RuntimeException("El mensaje del evento no puede estar vacio");

        Notification notification = new Notification();
        notification.setType(event.getType().name());
        notification.setTitle(event.getTitle() != null ? event.getTitle() : event.getType().name());
        notification.setMessage(event.getMessage());
        notification.setPerformedBy(event.getPerformedBy() != null ? event.getPerformedBy() : "system");
        notification.setSourceService(event.getSourceService() != null ? event.getSourceService() : "catalog-service");
        notification.setStatus("RECEIVED");
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = notificationGateway.save(notification);

        // Solo envia email cuando se crea un nuevo producto
        if (event.getType() == CatalogEvent.EventType.PRODUCT_CREATED) {
            emailSenderGateway.sendEmail(
                adminEmail,
                "SportShop - Nuevo producto en el catalogo",
                buildProductCreatedEmail(event)
            );
        }

        return saved;
    }

    public List<Notification> getAllNotifications() {
        return notificationGateway.findAll();
    }

    public Notification getNotificationById(Long id) {
        if (id == null || id <= 0)
            throw new RuntimeException("El ID debe ser un numero positivo");
        return notificationGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe una notificacion con id: " + id));
    }

    public List<Notification> getNotificationsByUser(String performedBy) {
        if (performedBy == null || performedBy.trim().isEmpty())
            throw new RuntimeException("El usuario no puede estar vacio");
        return notificationGateway.findByPerformedBy(performedBy);
    }

    public List<Notification> getNotificationsByType(String type) {
        if (type == null || type.trim().isEmpty())
            throw new RuntimeException("El tipo no puede estar vacio");
        return notificationGateway.findByType(type.toUpperCase());
    }

    public List<Notification> getNotificationsByStatus(String status) {
        if (status == null || status.trim().isEmpty())
            throw new RuntimeException("El estado no puede estar vacio");
        String upperStatus = status.toUpperCase();
        if (!upperStatus.equals("RECEIVED") && !upperStatus.equals("READ"))
            throw new RuntimeException("Estado invalido. Use RECEIVED o READ");
        return notificationGateway.findByStatus(upperStatus);
    }

    public List<Notification> getNotificationsBySourceService(String sourceService) {
        if (sourceService == null || sourceService.trim().isEmpty())
            throw new RuntimeException("El servicio origen no puede estar vacio");
        return notificationGateway.findBySourceService(sourceService);
    }

    public Notification markAsRead(Long id) {
        if (id == null || id <= 0)
            throw new RuntimeException("El ID debe ser un numero positivo");
        notificationGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe una notificacion con id: " + id));
        return notificationGateway.markAsRead(id);
    }

    private String buildProductCreatedEmail(CatalogEvent event) {
        String title   = event.getTitle()       != null ? event.getTitle()       : "PRODUCT_CREATED";
        String message = event.getMessage()     != null ? event.getMessage()     : "";
        String by      = event.getPerformedBy() != null ? event.getPerformedBy() : "system";

        return "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;'>"
             + "<div style='background:#0a0a0a;padding:24px;text-align:center;'>"
             + "<h1 style='color:#fafafa;font-size:28px;letter-spacing:4px;margin:0;'>"
             + "SPORT<span style='color:#1d6db5;'>SHOP</span></h1></div>"
             + "<div style='background:#f9fafb;padding:32px;'>"
             + "<h2 style='color:#111827;'>Nuevo producto en el catalogo</h2>"
             + "<div style='background:white;border-left:4px solid #1d6db5;"
             + "border-radius:8px;padding:20px;margin:16px 0;'>"
             + "<p><strong>Evento:</strong> " + title + "</p>"
             + "<p><strong>Detalle:</strong> " + message + "</p>"
             + "<p style='color:#6b7280;font-size:13px;'><strong>Registrado por:</strong> " + by + "</p>"
             + "</div>"
             + "<p style='color:#9ca3af;font-size:12px;'>Mensaje automatico de SportShop.</p>"
             + "</div></div>";
    }
}
