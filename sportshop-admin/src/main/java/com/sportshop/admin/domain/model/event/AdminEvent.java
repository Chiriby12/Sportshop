package com.sportshop.admin.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Evento de dominio que el admin publica al microservicio de notificaciones.
 * Estructura idéntica al CatalogEvent del catálogo para que notifications
 * pueda deserializarlo sin cambios.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminEvent {

    public enum EventType {
        // Eventos de productos (admin CRUD)
        PRODUCT_CREATED,
        PRODUCT_UPDATED,
        PRODUCT_DELETED,
        // Eventos de usuarios (admin CRUD)
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_ROLE_CHANGED
    }

    private EventType type;
    private String title;
    private String message;
    private String performedBy;   // documento del admin que ejecutó la acción
    private Object payload;
    private LocalDateTime timestamp;

    public static AdminEvent of(EventType type, String title, String message,
                                String performedBy, Object payload) {
        return new AdminEvent(type, title, message, performedBy, payload, LocalDateTime.now());
    }
}
