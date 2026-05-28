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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductController.class, CartController.class})
@Import({SecurityConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
@DisplayName("SecurityConfig — Tests de seguridad del catálogo")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ProductUseCase productUseCase;
    @MockitoBean private CartUseCase cartUseCase;
    @MockitoBean private ProductMapper productMapper;
    @MockitoBean private CartMapper cartMapper;

    @Test
    @DisplayName("GET /products es público — no requiere token")
    void publicEndpoint_noToken_200() throws Exception {
        // El useCase lanza excepción → GlobalExceptionHandler retorna 400, no 401
        // Lo importante es que NO es 401 (sí pasa el filtro de seguridad)
        mockMvc.perform(get("/api/sportshop/catalog/products"))
                .andExpect(result ->
                        org.junit.jupiter.api.Assertions.assertNotEquals(
                                401, result.getResponse().getStatus()));
    }

    @Test
    @DisplayName("GET /products/all requiere autenticación — retorna 401 sin token")
    void adminEndpoint_noToken_401() throws Exception {
        mockMvc.perform(get("/api/sportshop/catalog/products/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /cart requiere autenticación — retorna 401 sin token")
    void cartEndpoint_noToken_401() throws Exception {
        mockMvc.perform(post("/api/sportshop/catalog/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Swagger UI es accesible sin token")
    void swaggerUi_accessible() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("GET /api-docs es accesible sin token")
    void apiDocs_accessible() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }
}
