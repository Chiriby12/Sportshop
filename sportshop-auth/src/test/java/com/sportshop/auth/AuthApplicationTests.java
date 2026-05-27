package com.sportshop.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AuthApplicationTests {

    @Test
    @DisplayName("main: la clase de aplicación existe")
    void contextLoads() {
        assertDoesNotThrow(() -> new AuthApplication());
    }
}
