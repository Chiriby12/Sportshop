package com.sportshop.auth.application.config;

import com.sportshop.auth.infraestructure.entry_points.JwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig - Test de Cobertura de Excepciones")
class SecurityConfigTest {

    @Mock private JwtFilter jwtFilter;
    @InjectMocks private SecurityConfig securityConfig;

    @Test
    @DisplayName("corsConfigurationSource: se crea correctamente")
    void corsConfigurationSource_creacion() {
        assertNotNull(securityConfig.corsConfigurationSource());
    }

    @Test
    @DisplayName("Debe ejecutar de forma real las lambdas de exceptionHandling e inyectar cobertura")
    @SuppressWarnings("unchecked")
    void debeCubrirLambdasDeExcepciones() throws Exception {
        // 1. Crear mocks para el encadenamiento fluido de HttpSecurity
        HttpSecurity httpMock = mock(HttpSecurity.class);
        ExceptionHandlingConfigurer<HttpSecurity> exceptionConfigurerMock = mock(ExceptionHandlingConfigurer.class);

        // Permitir el encadenamiento de HttpSecurity básico
        when(httpMock.csrf(any())).thenReturn(httpMock);
        when(httpMock.cors(any())).thenReturn(httpMock);
        when(httpMock.sessionManagement(any())).thenReturn(httpMock);
        when(httpMock.authorizeHttpRequests(any())).thenReturn(httpMock);
        when(httpMock.addFilterBefore(any(), any())).thenReturn(httpMock);

        // 🔥 SOLUCIÓN AL NPE: Permitir el encadenamiento dentro del exceptionConfigurer
        // Cuando se llame a .authenticationEntryPoint(...), devolvemos el mismo mock para que no de null
        when(exceptionConfigurerMock.authenticationEntryPoint(any())).thenReturn(exceptionConfigurerMock);
        when(exceptionConfigurerMock.accessDeniedHandler(any())).thenReturn(exceptionConfigurerMock);

        // 2. Interceptamos la llamada a .exceptionHandling(ex -> ...)
        when(httpMock.exceptionHandling(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<ExceptionHandlingConfigurer<HttpSecurity>> customizer = invocation.getArgument(0);
            // Esto ejecuta de forma real tu bloque de código de producción: ex -> ex...
            customizer.customize(exceptionConfigurerMock);
            return httpMock;
        });

        // 3. Preparar los capturadores para atrapar las lambdas físicas de tu SecurityConfig
        ArgumentCaptor<AuthenticationEntryPoint> entryPointCaptor = ArgumentCaptor.forClass(AuthenticationEntryPoint.class);
        ArgumentCaptor<AccessDeniedHandler> accessDeniedCaptor = ArgumentCaptor.forClass(AccessDeniedHandler.class);

        // 4. Ejecutamos el método de producción para detonar las capturas
        securityConfig.securityFilterChain(httpMock);

        // Verificamos e interceptamos el EntryPoint y el AccessDeniedHandler reales
        verify(exceptionConfigurerMock).authenticationEntryPoint(entryPointCaptor.capture());
        verify(exceptionConfigurerMock).accessDeniedHandler(accessDeniedCaptor.capture());

        // Extraemos las funciones reales que escribiste en producción (los bloques de JSON)
        AuthenticationEntryPoint realEntryPoint = entryPointCaptor.getValue();
        AccessDeniedHandler realAccessDeniedHandler = accessDeniedCaptor.getValue();

        // 5. Simular los objetos request y response para la escritura del JSON
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        when(mockResponse.getWriter()).thenReturn(writer);

        // 6. INVOCACIÓN DIRECTA DE LAS LAMBDAS (JaCoCo pintará tus líneas de color VERDE aquí)
        realEntryPoint.commence(mockRequest, mockResponse, mock(AuthenticationException.class));
        realAccessDeniedHandler.handle(mockRequest, mockResponse, mock(AccessDeniedException.class));

        // Validaciones de control final
        verify(mockResponse).setStatus(401);
        verify(mockResponse).setStatus(403);
    }
}