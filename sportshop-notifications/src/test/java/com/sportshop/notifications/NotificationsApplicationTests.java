package com.sportshop.notifications;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("NotificationsApplication — Test de clase principal")
class NotificationsApplicationTests {

    @Test
    @DisplayName("La clase NotificationsApplication se instancia sin errores")
    void contextLoads() {
        assertDoesNotThrow(() -> new NotificationsApplication());
    }
}
