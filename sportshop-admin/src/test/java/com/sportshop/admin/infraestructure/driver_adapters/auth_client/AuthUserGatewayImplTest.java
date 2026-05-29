package com.sportshop.admin.infraestructure.driver_adapters.auth_client;

import com.sportshop.admin.domain.model.AdminUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUserGatewayImpl - Tests del adaptador de usuarios")
class AuthUserGatewayImplTest {

    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private AuthUserGatewayImpl gateway;

    private AdminUser user;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        gateway = new AuthUserGatewayImpl(webClientBuilder, "http://localhost:8080/api/sportshop/auth");

        user = new AdminUser("12345678", "Ana García", "ana@test.com", "3001234567", 25, "USER", true);

        // Token en contexto de seguridad
        var auth = new UsernamePasswordAuthenticationToken("ana@test.com", "token123");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ─── Helpers para construir respuestas simuladas ──────────────────────────

    private Map<String, Object> buildUserMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("document", "12345678");
        data.put("name", "Ana García");
        data.put("email", "ana@test.com");
        data.put("telephone", "3001234567");
        data.put("age", 25);
        data.put("role", "USER");
        return data;
    }

    private Map<String, Object> buildResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return response;
    }

    // ─── save → create (usuario nuevo) ───────────────────────────────────────

    @Test
    @DisplayName("save: crea usuario nuevo cuando no existe (existsByDocument = false)")
    @SuppressWarnings("unchecked")
    void save_create_success() {
        // GET → findByDocument → null data → existsByDocument = false
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(null)));

        // POST → create
        WebClient.RequestBodyUriSpec postUri = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec postBody = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec postHeaders = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec postResponse = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(postUri);
        when(postUri.uri(anyString())).thenReturn(postBody);
        when(postBody.bodyValue(any())).thenReturn(postHeaders);
        when(postHeaders.retrieve()).thenReturn(postResponse);
        when(postResponse.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(buildUserMap())));

        AdminUser result = gateway.save(user);

        assertThat(result).isNotNull();
        assertThat(result.getDocument()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("save: actualiza usuario cuando ya existe (existsByDocument = true)")
    @SuppressWarnings("unchecked")
    void save_update_success() {
        // GET → findByDocument → retorna usuario → existsByDocument = true
        WebClient.RequestHeadersUriSpec getUri = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec getHeaders = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec getResponse = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(getUri);
        when(getUri.uri(anyString(), any(Object[].class))).thenReturn(getHeaders);
        when(getHeaders.header(anyString(), anyString())).thenReturn(getHeaders);
        when(getHeaders.retrieve()).thenReturn(getResponse);
        when(getResponse.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(buildUserMap())));

        // PUT → update
        WebClient.RequestBodyUriSpec putUri = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec putBody = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec putHeaders = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec putResponse = mock(WebClient.ResponseSpec.class);

        when(webClient.put()).thenReturn(putUri);
        when(putUri.uri(anyString(), any(Object[].class))).thenReturn(putBody);
        when(putBody.header(anyString(), anyString())).thenReturn(putBody);
        when(putBody.bodyValue(any())).thenReturn(putHeaders);
        when(putHeaders.retrieve()).thenReturn(putResponse);
        when(putResponse.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(buildUserMap())));

        AdminUser result = gateway.save(user);

        assertThat(result).isNotNull();
        assertThat(result.getDocument()).isEqualTo("12345678");
    }

    // ─── findByDocument ───────────────────────────────────────────────────────

    @Test
    @DisplayName("findByDocument: retorna usuario cuando existe")
    @SuppressWarnings("unchecked")
    void findByDocument_found() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(buildUserMap())));

        Optional<AdminUser> result = gateway.findByDocument("12345678");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("ana@test.com");
    }

    @Test
    @DisplayName("findByDocument: retorna empty cuando no existe (404)")
    @SuppressWarnings("unchecked")
    void findByDocument_notFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenThrow(WebClientResponseException.NotFound.class);

        Optional<AdminUser> result = gateway.findByDocument("99999");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByDocument: retorna empty en error genérico")
    @SuppressWarnings("unchecked")
    void findByDocument_genericError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        Optional<AdminUser> result = gateway.findByDocument("12345678");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByDocument: retorna empty cuando response data es null")
    @SuppressWarnings("unchecked")
    void findByDocument_nullData() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(null)));

        Optional<AdminUser> result = gateway.findByDocument("12345678");

        assertThat(result).isEmpty();
    }

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll: retorna lista de usuarios")
    @SuppressWarnings("unchecked")
    void findAll_success() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(List.of(buildUserMap()))));

        List<AdminUser> result = gateway.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDocument()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("findAll: retorna lista vacía cuando data es null")
    @SuppressWarnings("unchecked")
    void findAll_nullData() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(new HashMap<>()));

        List<AdminUser> result = gateway.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll: retorna lista vacía en error de conexión")
    @SuppressWarnings("unchecked")
    void findAll_connectionError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        List<AdminUser> result = gateway.findAll();

        assertThat(result).isEmpty();
    }

    // ─── findByEmail ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByEmail: retorna usuario cuando coincide email")
    @SuppressWarnings("unchecked")
    void findByEmail_found() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(List.of(buildUserMap()))));

        Optional<AdminUser> result = gateway.findByEmail("ana@test.com");

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("findByEmail: retorna empty cuando no coincide email")
    @SuppressWarnings("unchecked")
    void findByEmail_notFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(List.of(buildUserMap()))));

        Optional<AdminUser> result = gateway.findByEmail("otro@test.com");

        assertThat(result).isEmpty();
    }

    // ─── findByRole ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByRole: filtra usuarios por rol")
    @SuppressWarnings("unchecked")
    void findByRole_success() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(List.of(buildUserMap()))));

        List<AdminUser> result = gateway.findByRole("USER");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findByRole: retorna vacío si no hay usuarios con ese rol")
    @SuppressWarnings("unchecked")
    void findByRole_noMatch() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(List.of(buildUserMap()))));

        List<AdminUser> result = gateway.findByRole("ADMIN");

        assertThat(result).isEmpty();
    }

    // ─── deleteByDocument ─────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteByDocument: ejecuta DELETE correctamente")
    @SuppressWarnings("unchecked")
    void deleteByDocument_success() {
        WebClient.RequestHeadersUriSpec deleteSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(webClient.delete()).thenReturn(deleteSpec);
        when(deleteSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        assertThatCode(() -> gateway.deleteByDocument("12345678")).doesNotThrowAnyException();
    }

    // ─── existsByDocument / existsByEmail ─────────────────────────────────────

    @Test
    @DisplayName("existsByDocument: retorna true si existe")
    @SuppressWarnings("unchecked")
    void existsByDocument_true() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(buildUserMap())));

        assertThat(gateway.existsByDocument("12345678")).isTrue();
    }

    @Test
    @DisplayName("existsByDocument: retorna false si no existe")
    @SuppressWarnings("unchecked")
    void existsByDocument_false() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenThrow(WebClientResponseException.NotFound.class);

        assertThat(gateway.existsByDocument("99999")).isFalse();
    }

    @Test
    @DisplayName("existsByEmail: retorna true si existe")
    @SuppressWarnings("unchecked")
    void existsByEmail_true() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(List.of(buildUserMap()))));

        assertThat(gateway.existsByEmail("ana@test.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail: retorna false si no existe")
    @SuppressWarnings("unchecked")
    void existsByEmail_false() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(List.of(buildUserMap()))));

        assertThat(gateway.existsByEmail("otro@test.com")).isFalse();
    }

    // ─── obtenerToken sin credenciales ────────────────────────────────────────

    @Test
    @DisplayName("findAll: funciona sin token en contexto de seguridad")
    @SuppressWarnings("unchecked")
    void findAll_sinToken() {
        SecurityContextHolder.clearContext();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(List.of(buildUserMap()))));

        List<AdminUser> result = gateway.findAll();

        assertThat(result).hasSize(1);
    }

    // ─── mapToAdminUser edge cases ────────────────────────────────────────────

    @Test
    @DisplayName("findAll: mapea correctamente cuando age no es Number")
    @SuppressWarnings("unchecked")
    void findAll_ageNotNumber() {
        Map<String, Object> userData = new HashMap<>(buildUserMap());
        userData.put("age", "no-es-numero");
        userData.put("role", null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(buildResponse(List.of(userData))));

        List<AdminUser> result = gateway.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAge()).isNull();
        assertThat(result.get(0).getRole()).isEqualTo("USER");
    }
}