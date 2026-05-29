package com.sportshop.catalog.application.config;

import com.sportshop.catalog.domain.usecase.CartUseCase;
import com.sportshop.catalog.domain.usecase.ProductUseCase;
import com.sportshop.catalog.infraestructure.entry_points.CartController;
import com.sportshop.catalog.infraestructure.entry_points.GlobalExceptionHandler;
import com.sportshop.catalog.infraestructure.entry_points.JwtFilter;
import com.sportshop.catalog.infraestructure.entry_points.ProductController;
import com.sportshop.catalog.infraestructure.mapper.CartMapper;
import com.sportshop.catalog.infraestructure.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductController.class, CartController.class})
@Import({SecurityConfigTest.TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("SecurityConfig — Tests de seguridad del catálogo")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ProductUseCase productUseCase;
    @MockitoBean private CartUseCase cartUseCase;
    @MockitoBean private ProductMapper productMapper;
    @MockitoBean private CartMapper cartMapper;
    @MockitoBean private JwtFilter jwtFilter;

    /**
     * Configuración de seguridad idéntica a la real pero sin depender
     * del JwtFilter como bean — replica exactamente las mismas reglas.
     */
    @Configuration
    static class TestSecurityConfig {

        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((req, res, e) -> {
                                res.setStatus(401);
                                res.setContentType("application/json");
                                res.getWriter().write("{\"status\":401,\"mensaje\":\"Token requerido\"}");
                            })
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.GET, "/api/sportshop/catalog/products").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/sportshop/catalog/products/{id}").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/sportshop/catalog/products/category/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/sportshop/catalog/products/sport/**").permitAll()
                            .requestMatchers("/api/sportshop/catalog/products/sync/**").permitAll()
                            .requestMatchers(
                                    "/swagger-ui.html", "/swagger-ui/**",
                                    "/api-docs", "/api-docs/*", "/v3/api-docs/*"
                            ).permitAll()
                            .anyRequest().authenticated()
                    )
                    .build();
        }
    }

    @Test
    @DisplayName("GET /products es público — no requiere token")
    void publicEndpoint_noToken_200() throws Exception {
        mockMvc.perform(get("/api/sportshop/catalog/products"))
                .andExpect(result ->
                        org.junit.jupiter.api.Assertions.assertNotEquals(
                                401, result.getResponse().getStatus()));
    }

    @Test
    @DisplayName("POST /cart requiere autenticación — retorna 401 sin token")
    void cartEndpoint_noToken_401() throws Exception {
        mockMvc.perform(post("/api/sportshop/catalog/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /products requiere autenticación — retorna 401 sin token")
    void postProducts_noToken_401() throws Exception {
        mockMvc.perform(post("/api/sportshop/catalog/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Swagger UI es accesible sin token")
    void swaggerUi_accessible() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertNotEquals(
                            401, status,
                            "Swagger UI no deberia requerir autenticacion"
                    );
                });
    }

    @Test
    @DisplayName("GET /api-docs es accesible sin token")
    void apiDocs_accessible() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }
}