package com.sportshop.notifications.application.config;

import com.sportshop.notifications.domain.model.gateway.EmailSenderGateway;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import com.sportshop.notifications.domain.usecase.NotificationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("UseCaseConfig - Test de Cobertura Total (Notificaciones)")
class UseCaseConfigTest {


    @Mock
    private NotificationGateway notificationGateway;

    @Mock
    private EmailSenderGateway emailSenderGateway;


    @InjectMocks
    private UseCaseConfig useCaseConfig;

    @Test
    @DisplayName("Debe instanciar NotificationUseCase correctamente")
    void notificationUseCase_debeRetornarInstancia() {

        String dummyAdminEmail = "admin@sportshop.com";


        NotificationUseCase useCase = useCaseConfig.notificationUseCase(
                notificationGateway,
                emailSenderGateway,
                dummyAdminEmail
        );


        assertNotNull(useCase, "La instancia de NotificationUseCase no debe ser nula");
    }
}