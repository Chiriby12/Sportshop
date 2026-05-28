package com.sportshop.admin.domain.model.gateway;

import com.sportshop.admin.domain.model.event.AdminEvent;

/**
 * Puerto de salida para publicar eventos al microservicio de notificaciones.
 * El dominio llama a este puerto; la infraestructura decide cómo enviarlo (HTTP WebClient).
 */
public interface EventPublisherGateway {
    void publish(AdminEvent event);
}
