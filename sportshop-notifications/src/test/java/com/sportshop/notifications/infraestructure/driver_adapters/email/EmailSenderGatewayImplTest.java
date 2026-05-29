package com.sportshop.notifications.infraestructure.driver_adapters.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailSenderGatewayImpl - Tests del adaptador de email")
class EmailSenderGatewayImplTest {

    @Test
    @DisplayName("sendEmail: envia correctamente cuando el servicio responde OK")
    void sendEmail_ok() {
        WebClient.Builder builder               = mock(WebClient.Builder.class);
        WebClient webClient                     = mock(WebClient.class);
        WebClient.RequestBodyUriSpec uri        = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> headers = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec resp             = mock(WebClient.ResponseSpec.class);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.defaultHeader(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(uri);
        when(uri.uri(anyString())).thenReturn(uri);
        doReturn(headers).when(uri).bodyValue(any());
        when(headers.retrieve()).thenReturn(resp);
        when(resp.bodyToMono(String.class)).thenReturn(Mono.just("{\"id\":\"abc123\"}"));

        EmailSenderGatewayImpl impl =
                new EmailSenderGatewayImpl(builder, "re_testkey", "onboarding@resend.dev");

        assertDoesNotThrow(() ->
                impl.sendEmail("admin@test.com", "Test subject", "<p>Test body</p>"));
    }

    @Test
    @DisplayName("sendEmail: no lanza excepcion si el servicio devuelve error")
    void sendEmail_serviceError() {
        WebClient.Builder builder               = mock(WebClient.Builder.class);
        WebClient webClient                     = mock(WebClient.class);
        WebClient.RequestBodyUriSpec uri        = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> headers = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec resp             = mock(WebClient.ResponseSpec.class);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.defaultHeader(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(uri);
        when(uri.uri(anyString())).thenReturn(uri);
        doReturn(headers).when(uri).bodyValue(any());
        when(headers.retrieve()).thenReturn(resp);
        when(resp.bodyToMono(String.class))
                .thenReturn(Mono.error(new RuntimeException("403 Forbidden")));

        EmailSenderGatewayImpl impl =
                new EmailSenderGatewayImpl(builder, "re_testkey", "onboarding@resend.dev");

        assertDoesNotThrow(() ->
                impl.sendEmail("admin@test.com", "Test subject", "<p>Test</p>"));
    }

    @Test
    @DisplayName("sendEmail: no lanza excepcion si ocurre error inesperado")
    void sendEmail_errorInesperado() {
        WebClient.Builder builder        = mock(WebClient.Builder.class);
        WebClient webClient              = mock(WebClient.class);
        WebClient.RequestBodyUriSpec uri = mock(WebClient.RequestBodyUriSpec.class);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.defaultHeader(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(uri);
        when(uri.uri(anyString())).thenThrow(new RuntimeException("Error inesperado"));

        EmailSenderGatewayImpl impl =
                new EmailSenderGatewayImpl(builder, "re_testkey", "onboarding@resend.dev");

        assertDoesNotThrow(() ->
                impl.sendEmail("admin@test.com", "Subject", "<p>Body</p>"));
    }
}