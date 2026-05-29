package com.sportshop.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

@DisplayName("CatalogApplication - Test de Cobertura Rápida")
class CatalogApplicationTests {



    @Test
    @DisplayName("Debe ejecutar el método main de la aplicación aislando el contexto")
    void mainMethodTest() {

        try (MockedStatic<SpringApplication> springApplicationMock = Mockito.mockStatic(SpringApplication.class)) {
            CatalogApplication.main(new String[]{});
            springApplicationMock.verify(() -> SpringApplication.run(CatalogApplication.class, new String[]{}));
        }
    }
}