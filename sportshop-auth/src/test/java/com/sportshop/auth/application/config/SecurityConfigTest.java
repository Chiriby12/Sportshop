package com.sportshop.auth.application.config;

import com.sportshop.auth.infraestructure.entry_points.JwtFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThatNoException;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock private JwtFilter jwtFilter;
    @InjectMocks private SecurityConfig securityConfig;

    @Test
    @DisplayName("corsConfigurationSource: se crea sin errores")
    void corsConfigurationSource_noExcepcion() {
        assertThatNoException().isThrownBy(() -> securityConfig.corsConfigurationSource());
    }
}
