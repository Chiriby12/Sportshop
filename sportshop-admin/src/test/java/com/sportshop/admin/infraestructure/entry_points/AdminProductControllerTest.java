package com.sportshop.admin.infraestructure.entry_points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshop.admin.application.config.OpenApiConfig;
import com.sportshop.admin.application.config.SecurityConfig;
import com.sportshop.admin.application.dto.AdminProductRequestDTO;
import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.domain.usecase.AdminProductUseCase;
import com.sportshop.admin.infraestructure.mapper.AdminProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminProductController.class)
@Import({SecurityConfig.class, OpenApiConfig.class, JwtFilter.class})
@DisplayName("AdminProductController - Tests de endpoints de productos")
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminProductUseCase productUseCase;

    @MockitoBean
    private AdminProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminProduct product;
    private AdminProductRequestDTO dto;

    @BeforeEach
    void setUp() {
        product = new AdminProduct(1L, "Camiseta Nike", "Camiseta deportiva",
                "Nike", "RUNNING", "ATLETISMO",
                new BigDecimal("85000"), 50, "https://img.com/cam.jpg", true);

        dto = new AdminProductRequestDTO("Camiseta Nike", "Camiseta deportiva",
                "Nike", "RUNNING", "ATLETISMO",
                new BigDecimal("85000"), 50, "https://img.com/cam.jpg", true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products: crea producto correctamente - ADMIN")
    void createProduct_exitoso() throws Exception {
        when(productUseCase.createProduct(any(), any())).thenReturn(product);

        mockMvc.perform(post("/api/sportshop/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /products: USER sin permisos retorna 403")
    void createProduct_sinPermisos() throws Exception {
        mockMvc.perform(post("/api/sportshop/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /products: sin token retorna 401")
    void createProduct_sinToken() throws Exception {
        mockMvc.perform(post("/api/sportshop/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products: error en validación de negocio retorna 400")
    void createProduct_errorNegocio() throws Exception {
        when(productUseCase.createProduct(any(), any()))
                .thenThrow(new RuntimeException("El nombre del producto no puede estar vacío"));

        mockMvc.perform(post("/api/sportshop/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /products: lista todos los productos incluye inactivos")
    void getAllProducts_exitoso() throws Exception {
        when(productUseCase.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/sportshop/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /products: USER sin permisos retorna 403")
    void getAllProducts_sinPermisos() throws Exception {
        mockMvc.perform(get("/api/sportshop/admin/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /products/active: lista solo productos activos")
    void getActiveProducts_exitoso() throws Exception {
        when(productUseCase.getActiveProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/sportshop/admin/products/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /products/{id}: retorna producto por ID")
    void getProductById_exitoso() throws Exception {
        when(productUseCase.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/sportshop/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Camiseta Nike"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /products/{id}: producto no existe retorna 400")
    void getProductById_noExiste() throws Exception {
        when(productUseCase.getProductById(99L))
                .thenThrow(new RuntimeException("No existe un producto con id: 99"));

        mockMvc.perform(get("/api/sportshop/admin/products/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /products/category/{category}: filtra por categoría")
    void getByCategory_exitoso() throws Exception {
        when(productUseCase.getProductsByCategory("RUNNING")).thenReturn(List.of(product));

        mockMvc.perform(get("/api/sportshop/admin/products/category/RUNNING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /products/category/{category}: categoría inválida retorna 400")
    void getByCategory_invalida() throws Exception {
        when(productUseCase.getProductsByCategory("  "))
                .thenThrow(new RuntimeException("La categoría no puede estar vacía"));

        mockMvc.perform(get("/api/sportshop/admin/products/category/  "))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /products/sport/{sport}: filtra por deporte")
    void getBySport_exitoso() throws Exception {
        when(productUseCase.getProductsBySport("ATLETISMO")).thenReturn(List.of(product));

        mockMvc.perform(get("/api/sportshop/admin/products/sport/ATLETISMO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /products/{id}: actualiza producto correctamente")
    void updateProduct_exitoso() throws Exception {
        when(productUseCase.updateProduct(eq(1L), any(), any())).thenReturn(product);

        mockMvc.perform(put("/api/sportshop/admin/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /products/{id}: producto no existe retorna 400")
    void updateProduct_noExiste() throws Exception {
        when(productUseCase.updateProduct(eq(99L), any(), any()))
                .thenThrow(new RuntimeException("No existe un producto con id: 99"));

        mockMvc.perform(put("/api/sportshop/admin/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /products/{id}: USER sin permisos retorna 403")
    void updateProduct_sinPermisos() throws Exception {
        mockMvc.perform(put("/api/sportshop/admin/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /products/{id}: elimina producto correctamente")
    void deleteProduct_exitoso() throws Exception {
        doNothing().when(productUseCase).deleteProduct(eq(1L), any());

        mockMvc.perform(delete("/api/sportshop/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Producto 1 eliminado correctamente"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /products/{id}: producto no existe retorna 400")
    void deleteProduct_noExiste() throws Exception {
        doThrow(new RuntimeException("No existe un producto con id: 99"))
                .when(productUseCase).deleteProduct(eq(99L), any());

        mockMvc.perform(delete("/api/sportshop/admin/products/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /products/{id}: USER sin permisos retorna 403")
    void deleteProduct_sinPermisos() throws Exception {
        mockMvc.perform(delete("/api/sportshop/admin/products/1"))
                .andExpect(status().isForbidden());
    }
}