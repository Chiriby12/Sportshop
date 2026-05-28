package com.sportshop.auth.infraestructure.security_encrypter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncrypterGatewayImplTest {

    private EncrypterGatewayImpl encrypter;

    @BeforeEach
    void setUp() {
        encrypter = new EncrypterGatewayImpl();
    }

    @Test
    @DisplayName("encrypt: genera hash distinto al texto plano")
    void encrypt_generaHash() {
        String hashed = encrypter.encrypt("MiClave123");
        assertNotNull(hashed);
        assertNotEquals("MiClave123", hashed);
    }

    @Test
    @DisplayName("matches: contraseña correcta retorna true")
    void matches_correcto() {
        String hashed = encrypter.encrypt("MiClave123");
        assertTrue(encrypter.matches("MiClave123", hashed));
    }

    @Test
    @DisplayName("matches: contraseña incorrecta retorna false")
    void matches_incorrecto() {
        String hashed = encrypter.encrypt("MiClave123");
        assertFalse(encrypter.matches("OtraClave", hashed));
    }

    @Test
    @DisplayName("encrypt: dos hashes del mismo texto son distintos (salt)")
    void encrypt_saltDistinto() {
        String hash1 = encrypter.encrypt("MiClave123");
        String hash2 = encrypter.encrypt("MiClave123");
        assertNotEquals(hash1, hash2);
    }
}
