package com.sportshop.notifications.domain.usecase;

import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.domain.model.event.CatalogEvent;
import com.sportshop.notifications.domain.model.gateway.EmailSenderGateway;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
public class NotificationUseCase {

    private final NotificationGateway notificationGateway;
    private final EmailSenderGateway emailSenderGateway;
    private final String adminEmail;

    public Notification receiveEvent(CatalogEvent event) {
        if (event == null)
            throw new RuntimeException("El evento no puede ser nulo");
        if (event.getType() == null)
            throw new RuntimeException("El tipo del evento no puede ser nulo");
        if (event.getMessage() == null || event.getMessage().trim().isEmpty())
            throw new RuntimeException("El mensaje del evento no puede estar vacío");

        Notification notification = new Notification();
        notification.setType(event.getType().name());
        notification.setTitle(event.getTitle() != null ? event.getTitle() : event.getType().name());
        notification.setMessage(event.getMessage());
        notification.setPerformedBy(event.getPerformedBy() != null ? event.getPerformedBy() : "system");
        notification.setSourceService("catalog-service");
        notification.setStatus("RECEIVED");
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = notificationGateway.save(notification);


        emailSenderGateway.sendEmail(
                adminEmail,
                "[SportShop] " + saved.getTitle(),
                "<h2>" + saved.getTitle() + "</h2>" +
                        "<p>" + saved.getMessage() + "</p>" +
                        "<p><small>Evento: " + saved.getType() + " — " + saved.getCreatedAt() + "</small></p>"
        );

        return saved;
    }

    public List<Notification> getAllNotifications() {
        return notificationGateway.findAll();
    }

    public Notification getNotificationById(Long id) {
        if (id == null || id <= 0)
            throw new RuntimeException("El ID debe ser un número positivo");
        return notificationGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe una notificación con id: " + id));
    }

    public List<Notification> getNotificationsByUser(String performedBy) {
        if (performedBy == null || performedBy.trim().isEmpty())
            throw new RuntimeException("El usuario no puede estar vacío");
        return notificationGateway.findByPerformedBy(performedBy);
    }

    public List<Notification> getNotificationsByType(String type) {
        if (type == null || type.trim().isEmpty())
            throw new RuntimeException("El tipo no puede estar vacío");
        return notificationGateway.findByType(type.toUpperCase());
    }

    public List<Notification> getNotificationsByStatus(String status) {
        if (status == null || status.trim().isEmpty())
            throw new RuntimeException("El estado no puede estar vacío");
        String upperStatus = status.toUpperCase();
        if (!upperStatus.equals("RECEIVED") && !upperStatus.equals("READ"))
            throw new RuntimeException("Estado inválido. Use RECEIVED o READ");
        return notificationGateway.findByStatus(upperStatus);
    }

    public List<Notification> getNotificationsBySourceService(String sourceService) {
        if (sourceService == null || sourceService.trim().isEmpty())
            throw new RuntimeException("El servicio origen no puede estar vacío");
        return notificationGateway.findBySourceService(sourceService);
    }

    public Notification markAsRead(Long id) {
        if (id == null || id <= 0)
            throw new RuntimeException("El ID debe ser un número positivo");
        notificationGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe una notificación con id: " + id));
        return notificationGateway.markAsRead(id);
    }
}