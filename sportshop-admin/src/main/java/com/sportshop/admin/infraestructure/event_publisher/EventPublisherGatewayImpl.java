package com.sportshop.admin.infraestructure.event_publisher;

import com.sportshop.admin.domain.model.event.AdminEvent;
import com.sportshop.admin.domain.model.gateway.EventPublisherGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", event.getType().name());
        payload.put("title", event.getTitle());
        payload.put("message", event.getMessage());
        payload.put("performedBy", event.getPerformedBy());
        payload.put("sourceService", "admin-service");
        payload.put("payload", event.getPayload());
        payload.put("timestamp", LocalDateTime.now().toString());

        webClient.post()
                .uri("/api/sportshop/notifications/receive")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Evento admin publicado: {} - {}", event.getType(), event.getTitle()))
                .doOnError(e -> log.warn("No se pudo enviar evento al servicio de notificaciones: {}", e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }
}