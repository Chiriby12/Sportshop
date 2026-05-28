package com.sportshop.catalog.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Evento de dominio que se publica al microservicio de notificaciones.
 * Representa cualquier acción relevante en el catálogo o carrito.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogEvent {

    public enum EventType {
        // Eventos de productos (CRUD)
        PRODUCT_CREATED,
        PRODUCT_UPDATED,
        PRODUCT_DELETED,
        // Eventos de carrito (usuario)
        CART_ITEM_ADDED,
        CART_ITEM_REMOVED,
        CART_ITEM_UPDATED,
        CART_PURCHASED
    }

    private EventType type;
    private String title;
    private String message;
    private String performedBy;   // documento o email de quien lo hizo
    private Object payload;       // datos extras (producto, carrito, etc.)
    private LocalDateTime timestamp;

    public static CatalogEvent of(EventType type, String title, String message, String performedBy, Object payload) {
        return new CatalogEvent(type, title, message, performedBy, payload, LocalDateTime.now());
    }
}
