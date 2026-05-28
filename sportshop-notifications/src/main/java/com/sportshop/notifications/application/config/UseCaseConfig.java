package com.sportshop.notifications.application.config;

import com.sportshop.notifications.domain.model.gateway.EmailSenderGateway;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import com.sportshop.notifications.domain.usecase.NotificationUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de casos de uso.
 * Arquitectura Hexagonal: conectamos los puertos con los adaptadores.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public NotificationUseCase notificationUseCase(
            NotificationGateway notificationGateway,
            EmailSenderGateway emailSenderGateway,
            @Value("${notification.admin-email}") String adminEmail) {
        return new NotificationUseCase(notificationGateway, emailSenderGateway, adminEmail);
    }
}