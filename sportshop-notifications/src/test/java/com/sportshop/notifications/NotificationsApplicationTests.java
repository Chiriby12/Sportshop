package com.sportshop.notifications;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

@DisplayName("NotificationsApplication - Test de Cobertura Rápida")
class NotificationsApplicationTests {

    @Test
    @DisplayName("Debe ejecutar el método main de la aplicación aislando el contexto")
    void mainMethodTest() {

        try (MockedStatic<SpringApplication> springApplicationMock = Mockito.mockStatic(SpringApplication.class)) {
            NotificationsApplication.main(new String[]{});
            springApplicationMock.verify(() -> SpringApplication.run(NotificationsApplication.class, new String[]{}));
        }
    }
}