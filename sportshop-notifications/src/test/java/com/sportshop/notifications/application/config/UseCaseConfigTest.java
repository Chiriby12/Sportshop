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

    // 1. Mockeamos las dependencias de los puertos
    @Mock
    private NotificationGateway notificationGateway;

    @Mock
    private EmailSenderGateway emailSenderGateway;

    // 2. Inyectamos los mocks en la clase de configuración para que se cubra el constructor
    @InjectMocks
    private UseCaseConfig useCaseConfig;

    @Test
    @DisplayName("Debe instanciar NotificationUseCase correctamente")
    void notificationUseCase_debeRetornarInstancia() {
        // Arrange: Simulamos el valor del properties que inyectaría Spring
        String dummyAdminEmail = "admin@sportshop.com";

        // Act: Llamamos al método físico pasándole los mocks y el string
        NotificationUseCase useCase = useCaseConfig.notificationUseCase(
                notificationGateway,
                emailSenderGateway,
                dummyAdminEmail
        );

        // Assert: Verificamos que pase por el return y cree el objeto
        assertNotNull(useCase, "La instancia de NotificationUseCase no debe ser nula");
    }
}