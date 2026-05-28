package com.sportshop.auth.infraestructure.security_encrypter;

import com.sportshop.auth.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtGatewayImplTest {

    private JwtGatewayImpl jwtGateway;
    private User usuario;

    @BeforeEach
    void setUp() {
        jwtGateway = new JwtGatewayImpl();
        ReflectionTestUtils.setField(jwtGateway, "secret",
                "3f8a2b1c9d7e4f6a0b5c8d2e1f7a3b4c9d6e5f8a2b1c0d7e4f3a6b5c8d9e2f1a");
        ReflectionTestUtils.setField(jwtGateway, "expirationMs", 3600000L);

        usuario = new User("1234567890", "Juan Pérez", "juan@mail.com",
                "hash", "3001234567", 25, "USER");
    }

    @Test
    @DisplayName("generateToken: genera un token no nulo")
    void generateToken_exitoso() {
        String token = jwtGateway.generateToken(usuario);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("extractEmail: extrae el email del token")
    void extractEmail() {
        String token = jwtGateway.generateToken(usuario);
        assertEquals("juan@mail.com", jwtGateway.extractEmail(token));
    }

    @Test
    @DisplayName("extractRole: extrae el rol del token")
    void extractRole() {
        String token = jwtGateway.generateToken(usuario);
        assertEquals("USER", jwtGateway.extractRole(token));
    }

    @Test
    @DisplayName("isTokenValid: token válido retorna true")
    void isTokenValid_valido() {
        String token = jwtGateway.generateToken(usuario);
        assertTrue(jwtGateway.isTokenValid(token));
    }

    @Test
    @DisplayName("isTokenValid: token inválido retorna false")
    void isTokenValid_invalido() {
        assertFalse(jwtGateway.isTokenValid("token.invalido.xxx"));
    }

    @Test
    @DisplayName("isTokenValid: token expirado retorna false")
    void isTokenValid_expirado() {
        ReflectionTestUtils.setField(jwtGateway, "expirationMs", -1L);
        String token = jwtGateway.generateToken(usuario);
        assertFalse(jwtGateway.isTokenValid(token));
    }
}
