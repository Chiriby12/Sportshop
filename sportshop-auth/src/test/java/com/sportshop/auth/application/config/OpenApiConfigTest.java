package com.sportshop.auth.application.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = OpenApiConfig.class) // Obliga a Spring a registrar la clase como configuración real
@DisplayName("OpenApiConfig - Test de Configuración")
class OpenApiConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("Debe cargar la configuración de OpenAPI en el contexto de Spring")
    void openApiConfig_debeCargarseEnContexto() {
        // Buscamos el bean directamente en el contenedor de Spring para asegurar el escaneo de la clase
        OpenApiConfig configBean = context.getBean(OpenApiConfig.class);

        assertNotNull(configBean, "El bean de OpenApiConfig debería estar registrado en el contexto");
    }
}