package com.sportshop.admin.infraestructure.event_publisher;

import com.sportshop.admin.domain.model.event.AdminEvent;
import com.sportshop.admin.domain.model.gateway.EventPublisherGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Adaptador conducido (Driven Adapter) para publicar eventos.
 * Envía el AdminEvent al microservicio de notificaciones (port 8083) via HTTP POST.
 * Si el servicio no está disponible, NO falla la operación principal (best-effort).
 *
 * El notifications-service espera un objeto con los campos:
 *   type, title, message, performedBy, payload, timestamp
 * que coincide exactamente con AdminEvent.
 */
@Component
@Slf4j
public class EventPublisherGatewayImpl implements EventPublisherGateway {

    private final WebClient webClient;

    public EventPublisherGatewayImpl(
            WebClient.Builder webClientBuilder,
            @Value("${notifications.service.url}") String notificationsUrl) {
        this.webClient = webClientBuilder.baseUrl(notificationsUrl).build();
    }

    @Override
    public void publish(AdminEvent event) {
        webClient.post()
                .uri("/api/sportshop/notifications/receive")
                .bodyValue(event)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Evento admin publicado: {} - {}", event.getType(), event.getTitle()))
                .doOnError(e -> log.warn("No se pudo enviar evento al servicio de notificaciones: {}", e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }
}
