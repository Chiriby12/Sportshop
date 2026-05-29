package com.sportshop.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@DisplayName("AuthApplication - Test de Contexto y Main")
class AuthApplicationTests {

    @Test
    @DisplayName("Debe cargar el contexto de Spring correctamente")
    void contextLoads() {
        // Verifica que todo el microservicio levanta bien sin caerse
    }

    @Test
    @DisplayName("Debe ejecutar el método main de la aplicación sin lanzar excepciones")
    void mainMethodTest() {

        assertDoesNotThrow(() -> AuthApplication.main(new String[]{}));
    }
}