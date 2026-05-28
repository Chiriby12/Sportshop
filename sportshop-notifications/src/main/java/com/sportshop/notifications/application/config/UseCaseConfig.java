package com.sportshop.notifications.application.config;

import com.sportshop.notifications.domain.model.gateway.EmailSenderGateway;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import com.sportshop.notifications.domain.usecase.NotificationUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuracion de casos de uso.
 * Arquitectura Hexagonal: conecta los puertos con sus adaptadores.
 * @EnableAsync habilita el envio asincrono de emails.
 */
@Configuration
@EnableAsync
public class UseCaseConfig {

    @Value("${notification.admin-email:admin@sportshop.com}")
    private String adminEmail;

    @Bean
    public NotificationUseCase notificationUseCase(
            NotificationGateway notificationGateway,
            EmailSenderGateway emailSenderGateway) {
        NotificationUseCase useCase = new NotificationUseCase(notificationGateway, emailSenderGateway);
        useCase.setAdminEmail(adminEmail);
        return useCase;
    }
}
