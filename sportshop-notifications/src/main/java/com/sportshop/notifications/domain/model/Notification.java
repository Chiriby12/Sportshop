package com.sportshop.notifications.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Modelo de dominio puro - sin anotaciones de Spring ni JPA.
 * Arquitectura Hexagonal: núcleo del hexágono.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private Long id;
    private String type;          // PRODUCT_CREATED, PRODUCT_UPDATED, PRODUCT_DELETED, CART_ITEM_ADDED, etc.
    private String title;
    private String message;
    private String performedBy;   // documento o email de quien generó el evento
    private String sourceService; // catalog, auth, etc.
    private String status;        // RECEIVED, READ
    private LocalDateTime createdAt;
}
