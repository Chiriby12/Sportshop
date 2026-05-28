package com.sportshop.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("AdminApplication — Test de clase principal")
class AdminApplicationTests {

    @Test
    @DisplayName("La clase AdminApplication se instancia sin errores")
    void contextLoads() {
        assertDoesNotThrow(() -> new AdminApplication());
    }
}
