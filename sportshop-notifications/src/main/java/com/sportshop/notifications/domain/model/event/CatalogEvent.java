package com.sportshop.notifications.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogEvent {

    public enum EventType {
        PRODUCT_CREATED,
        PRODUCT_UPDATED,
        PRODUCT_DELETED,
        CART_ITEM_ADDED,
        CART_ITEM_REMOVED,
        CART_ITEM_UPDATED,
        CART_PURCHASED
    }

    private EventType type;
    private String title;
    private String message;
    private String performedBy;
    private String sourceService; // catalog-service, auth-service, admin-service
    private Object payload;
    private LocalDateTime timestamp;
}
