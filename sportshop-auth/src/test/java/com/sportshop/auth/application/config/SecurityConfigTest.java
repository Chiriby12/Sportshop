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

        HttpSecurity httpMock = mock(HttpSecurity.class);
        ExceptionHandlingConfigurer<HttpSecurity> exceptionConfigurerMock = mock(ExceptionHandlingConfigurer.class);


        when(httpMock.csrf(any())).thenReturn(httpMock);
        when(httpMock.cors(any())).thenReturn(httpMock);
        when(httpMock.sessionManagement(any())).thenReturn(httpMock);
        when(httpMock.authorizeHttpRequests(any())).thenReturn(httpMock);
        when(httpMock.addFilterBefore(any(), any())).thenReturn(httpMock);


        when(exceptionConfigurerMock.authenticationEntryPoint(any())).thenReturn(exceptionConfigurerMock);
        when(exceptionConfigurerMock.accessDeniedHandler(any())).thenReturn(exceptionConfigurerMock);


        when(httpMock.exceptionHandling(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<ExceptionHandlingConfigurer<HttpSecurity>> customizer = invocation.getArgument(0);

            customizer.customize(exceptionConfigurerMock);
            return httpMock;
        });


        ArgumentCaptor<AuthenticationEntryPoint> entryPointCaptor = ArgumentCaptor.forClass(AuthenticationEntryPoint.class);
        ArgumentCaptor<AccessDeniedHandler> accessDeniedCaptor = ArgumentCaptor.forClass(AccessDeniedHandler.class);


        securityConfig.securityFilterChain(httpMock);


        verify(exceptionConfigurerMock).authenticationEntryPoint(entryPointCaptor.capture());
        verify(exceptionConfigurerMock).accessDeniedHandler(accessDeniedCaptor.capture());


        AuthenticationEntryPoint realEntryPoint = entryPointCaptor.getValue();
        AccessDeniedHandler realAccessDeniedHandler = accessDeniedCaptor.getValue();


        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        when(mockResponse.getWriter()).thenReturn(writer);


        realEntryPoint.commence(mockRequest, mockResponse, mock(AuthenticationException.class));
        realAccessDeniedHandler.handle(mockRequest, mockResponse, mock(AccessDeniedException.class));


        verify(mockResponse).setStatus(401);
        verify(mockResponse).setStatus(403);
    }
}