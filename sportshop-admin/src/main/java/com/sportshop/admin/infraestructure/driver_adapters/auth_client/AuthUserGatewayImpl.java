package com.sportshop.admin.infraestructure.driver_adapters.auth_client;

import com.sportshop.admin.domain.model.AdminUser;
import com.sportshop.admin.domain.model.gateway.AdminUserGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
@Slf4j
public class AuthUserGatewayImpl implements AdminUserGateway {

    private final WebClient webClient;

    public AuthUserGatewayImpl(
            WebClient.Builder builder,
            @Value("${auth.service.url}") String authUrl) {
        this.webClient = builder
                .baseUrl(authUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public AdminUser save(AdminUser user) {
        boolean exists = existsByDocument(user.getDocument());
        return exists ? update(user) : create(user);
    }

    private AdminUser create(AdminUser user) {
        Map<String, Object> body = Map.of(
                "document",  user.getDocument(),
                "name",      user.getName(),
                "email",     user.getEmail(),
                "password",  "SportShop2025!",
                "telephone", user.getTelephone() != null ? user.getTelephone() : "",
                "age",       user.getAge()       != null ? user.getAge()       : 18,
                "role",      user.getRole()      != null ? user.getRole()      : "USER"
        );

        Map<String, Object> response = webClient.post()
                .uri("/save")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        return extractUser(response);
    }

    private AdminUser update(AdminUser user) {
        Map<String, Object> body = buildUpdateBody(user);

        Map<String, Object> response = webClient.put()
                .uri("/update/{document}", user.getDocument())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + obtenerToken())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        return extractUser(response);
    }

    @Override
    public Optional<AdminUser> findByDocument(String document) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri("/get/{document}", document)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + obtenerToken())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            return Optional.ofNullable(extractUser(response));
        } catch (WebClientResponseException.NotFound | WebClientResponseException.BadRequest e) {
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Error consultando usuario {} en auth: {}", document, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<AdminUser> findByEmail(String email) {
        return findAll().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst();
    }

    @Override
    public List<AdminUser> findAll() {
        try {
            Map<String, Object> response = webClient.get()
                    .uri("/all")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + obtenerToken())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response == null || response.get("data") == null) return List.of();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("data");
            return list.stream().map(this::mapToAdminUser).toList();
        } catch (Exception e) {
            log.warn("Error listando usuarios desde auth: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<AdminUser> findByRole(String role) {
        return findAll().stream()
                .filter(u -> role.equalsIgnoreCase(u.getRole()))
                .toList();
    }

    @Override
    public void deleteByDocument(String document) {
        webClient.delete()
                .uri("/delete/{document}", document)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + obtenerToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public boolean existsByDocument(String document) {
        return findByDocument(document).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }



    private String obtenerToken() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() instanceof String token && !token.isBlank()) {
            return token;
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private AdminUser extractUser(Map<String, Object> response) {
        if (response == null || response.get("data") == null) return null;
        Object data = response.get("data");
        if (data instanceof Map) return mapToAdminUser((Map<String, Object>) data);
        return null;
    }

    private AdminUser mapToAdminUser(Map<String, Object> data) {
        AdminUser user = new AdminUser();
        user.setDocument(str(data, "document"));
        user.setName(str(data, "name"));
        user.setEmail(str(data, "email"));
        user.setTelephone(str(data, "telephone"));
        user.setAge(data.get("age") instanceof Number n ? n.intValue() : null);
        user.setRole(str(data, "role") != null ? str(data, "role") : "USER");
        user.setActive(true);
        return user;
    }

    private Map<String, Object> buildUpdateBody(AdminUser user) {
        return Map.of(
                "document",  user.getDocument(),
                "name",      user.getName()      != null ? user.getName()      : "",
                "email",     user.getEmail()     != null ? user.getEmail()     : "",
                "telephone", user.getTelephone() != null ? user.getTelephone() : "",
                "age",       user.getAge()       != null ? user.getAge()       : 18,
                "role",      user.getRole()      != null ? user.getRole()      : "USER"
        );
    }

    private String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }
}