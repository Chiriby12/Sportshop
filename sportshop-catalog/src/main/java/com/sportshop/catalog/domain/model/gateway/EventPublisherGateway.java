package com.sportshop.catalog.domain.model.gateway;

import com.sportshop.catalog.domain.model.event.CatalogEvent;

/**
 * Puerto de salida para publicar eventos al microservicio de notificaciones.
 * El dominio llama a este puerto; la infraestructura decide cómo enviarlo (HTTP, etc.).
 */
public interface EventPublisherGateway {
    void publish(CatalogEvent event);
}
