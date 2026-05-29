package com.sportshop.notifications.application.config;

import com.sportshop.notifications.domain.model.gateway.EmailSenderGateway;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import com.sportshop.notifications.domain.usecase.NotificationUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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