package com.sportshop.catalog.infraestructure.entry_points;

import com.sportshop.catalog.application.config.OpenApiConfig;
import com.sportshop.catalog.application.config.SecurityConfig;
import com.sportshop.catalog.domain.usecase.ProductUseCase;
import com.sportshop.catalog.infraestructure.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, OpenApiConfig.class, JwtFilter.class})
@DisplayName("GlobalExceptionHandler - Tests del manejador de errores")
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ProductUseCase productUseCase;
    @MockitoBean private ProductMapper productMapper;

    @Test
    @DisplayName("RuntimeException retorna 400 con mensaje")
    void handleRuntime_returns400() throws Exception {
        when(productUseCase.getProductById(1L))
                .thenThrow(new RuntimeException("Producto no encontrado"));

        mockMvc.perform(get("/api/sportshop/catalog/products/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado"));
    }

    @Test
    @DisplayName("AccessDeniedException retorna 403")
    @WithMockUser(roles = "USER")
    void handleForbidden_returns403() throws Exception {
        mockMvc.perform(get("/api/sportshop/catalog/products/all"))
                .andExpect(status().isForbidden());
    }
}
