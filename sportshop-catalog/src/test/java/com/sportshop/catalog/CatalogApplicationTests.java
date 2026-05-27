package com.sportshop.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("CatalogApplication — Test de clase principal")
class CatalogApplicationTests {

    @Test
    @DisplayName("La clase CatalogApplication se instancia sin errores")
    void contextLoads() {
        assertDoesNotThrow(() -> new CatalogApplication());
    }
}
