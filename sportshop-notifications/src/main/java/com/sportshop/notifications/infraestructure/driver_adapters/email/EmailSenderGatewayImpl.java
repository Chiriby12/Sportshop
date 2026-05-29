package com.sportshop.notifications.infraestructure.driver_adapters.email;

import com.sportshop.notifications.domain.model.gateway.EmailSenderGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Adaptador conducido - implementa el puerto EmailSenderGateway.
 * Usa la API HTTP de Resend en vez de SMTP.
 * Arquitectura Hexagonal: el dominio nunca ve esta clase, solo el puerto (interfaz).
 * Resend usa HTTPS (puerto 443) que Railway nunca bloquea.
 */
@Component
@Slf4j
public class EmailSenderGatewayImpl implements EmailSenderGateway {

    private final WebClient webClient;
    private final String from;

    public EmailSenderGatewayImpl(
            WebClient.Builder webClientBuilder,
            @Value("${resend.api-key}") String apiKey,
            @Value("${resend.from}") String from) {
        this.from = from;
        this.webClient = webClientBuilder
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            Map<String, Object> payload = Map.of(
                    "from", from,
                    "to", List.of(to),
                    "subject", subject,
                    "html", body
            );

            webClient.post()
                    .uri("/emails")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(res -> log.info("Email enviado a {} via Resend. Id: {}", to, res))
                    .doOnError(e -> log.warn("No se pudo enviar email a {} via Resend: {}", to, e.getMessage()))
                    .onErrorComplete()
                    .subscribe();
        } catch (Exception e) {
            log.warn("Error al preparar email para {}: {}", to, e.getMessage());
        }
    }
}