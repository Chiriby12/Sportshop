package com.sportshop.notifications.application.config;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import com.sportshop.notifications.domain.usecase.NotificationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de casos de uso.
 * Arquitectura Hexagonal: conectamos los puertos con los adaptadores.
 */
@Configuration
public class UseCaseConfig {
    @Bean
    public NotificationUseCase notificationUseCase(NotificationGateway notificationGateway) {
        return new NotificationUseCase(notificationGateway);
    }
}
