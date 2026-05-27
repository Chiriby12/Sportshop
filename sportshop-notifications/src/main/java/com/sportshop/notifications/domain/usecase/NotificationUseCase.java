package com.sportshop.notifications.domain.usecase;

import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.domain.model.event.CatalogEvent;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso del dominio de Notificaciones.
 * Arquitectura Hexagonal: NÚCLEO del hexágono.
 * Sin dependencias de Spring ni JPA.
 */
@RequiredArgsConstructor
public class NotificationUseCase {

    private final NotificationGateway notificationGateway;

    /**
     * Recibe un evento del catálogo u otro microservicio y lo persiste como notificación.
     */
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

        return notificationGateway.save(notification);
    }

    /**
     * Lista todas las notificaciones del sistema.
     */
    public List<Notification> getAllNotifications() {
        return notificationGateway.findAll();
    }

    /**
     * Obtiene una notificación por ID.
     */
    public Notification getNotificationById(Long id) {
        if (id == null || id <= 0)
            throw new RuntimeException("El ID debe ser un número positivo");
        return notificationGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe una notificación con id: " + id));
    }

    /**
     * Obtiene las notificaciones de un usuario (por documento o email).
     */
    public List<Notification> getNotificationsByUser(String performedBy) {
        if (performedBy == null || performedBy.trim().isEmpty())
            throw new RuntimeException("El usuario no puede estar vacío");
        return notificationGateway.findByPerformedBy(performedBy);
    }

    /**
     * Filtra por tipo de evento.
     */
    public List<Notification> getNotificationsByType(String type) {
        if (type == null || type.trim().isEmpty())
            throw new RuntimeException("El tipo no puede estar vacío");
        return notificationGateway.findByType(type.toUpperCase());
    }

    /**
     * Filtra por estado: RECEIVED o READ.
     */
    public List<Notification> getNotificationsByStatus(String status) {
        if (status == null || status.trim().isEmpty())
            throw new RuntimeException("El estado no puede estar vacío");
        String upperStatus = status.toUpperCase();
        if (!upperStatus.equals("RECEIVED") && !upperStatus.equals("READ"))
            throw new RuntimeException("Estado inválido. Use RECEIVED o READ");
        return notificationGateway.findByStatus(upperStatus);
    }

    /**
     * Filtra por servicio origen.
     */
    public List<Notification> getNotificationsBySourceService(String sourceService) {
        if (sourceService == null || sourceService.trim().isEmpty())
            throw new RuntimeException("El servicio origen no puede estar vacío");
        return notificationGateway.findBySourceService(sourceService);
    }

    /**
     * Marca una notificación como leída.
     */
    public Notification markAsRead(Long id) {
        if (id == null || id <= 0)
            throw new RuntimeException("El ID debe ser un número positivo");
        // Verificar que existe
        notificationGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe una notificación con id: " + id));
        return notificationGateway.markAsRead(id);
    }
}
