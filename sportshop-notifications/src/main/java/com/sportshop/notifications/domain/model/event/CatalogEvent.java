package com.sportshop.notifications.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Representa el evento que llega desde cualquier microservicio
 * (catálogo o admin). Debe contener todos los tipos posibles
 * para que Jackson pueda deserializarlos correctamente.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogEvent {

    public enum EventType {
        // Eventos del catálogo
        PRODUCT_CREATED,
        PRODUCT_UPDATED,
        PRODUCT_DELETED,
        CART_ITEM_ADDED,
        CART_ITEM_REMOVED,
        CART_ITEM_UPDATED,
        CART_PURCHASED,
        // Eventos del admin (usuarios)
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_ROLE_CHANGED
    }

    private EventType type;
    private String title;
    private String message;
    private String performedBy;
    private Object payload;
    private LocalDateTime timestamp;
}