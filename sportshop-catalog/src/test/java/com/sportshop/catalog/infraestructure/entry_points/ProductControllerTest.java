package com.sportshop.catalog.infraestructure.entry_points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshop.catalog.application.config.OpenApiConfig;
import com.sportshop.catalog.application.config.SecurityConfig;
import com.sportshop.catalog.application.dto.ProductRequestDTO;
import com.sportshop.catalog.application.dto.ProductResponseDTO;
import com.sportshop.catalog.domain.model.Product;
import com.sportshop.catalog.domain.usecase.ProductUseCase;
import com.sportshop.catalog.infraestructure.mapper.ProductMapper;
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

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, OpenApiConfig.class, JwtFilter.class})
@DisplayName("ProductController - Tests de integración HTTP")
class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private ProductUseCase productUseCase;
    @MockitoBean private ProductMapper productMapper;

    private Product product;
    private ProductResponseDTO responseDTO;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Zapatillas Nike", "Para correr", "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("150.00"), 10, null, true);
        responseDTO = new ProductResponseDTO(1L, "Zapatillas Nike", "Para correr", "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("150.00"), 10, null, true);
        requestDTO = new ProductRequestDTO("Zapatillas Nike", "Para correr", "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("150.00"), 10, null, true);
    }

    // ─── GET público - sin auth ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /products - público, retorna productos activos")
    void getActiveProducts_public_200() throws Exception {
        when(productUseCase.getActiveProducts()).thenReturn(List.of(product));
        when(productMapper.toResponseDTO(any())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/sportshop/catalog/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].name").value("Zapatillas Nike"));
    }

    @Test
    @DisplayName("GET /products/{id} - público, retorna producto por id")
    void getProductById_public_200() throws Exception {
        when(productUseCase.getProductById(1L)).thenReturn(product);
        when(productMapper.toResponseDTO(any())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/sportshop/catalog/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("GET /products/{id} - lanza error si no existe")
    void getProductById_notFound_400() throws Exception {
        when(productUseCase.getProductById(99L)).thenThrow(new RuntimeException("No existe un producto con id: 99"));

        mockMvc.perform(get("/api/sportshop/catalog/products/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /products/category/{cat} - público")
    void getByCategory_public_200() throws Exception {
        when(productUseCase.getProductsByCategory("RUNNING")).thenReturn(List.of(product));
        when(productMapper.toResponseDTO(any())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/sportshop/catalog/products/category/RUNNING"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /products/sport/{sport} - público")
    void getBySport_public_200() throws Exception {
        when(productUseCase.getProductsBySport("FUTBOL")).thenReturn(List.of(product));
        when(productMapper.toResponseDTO(any())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/sportshop/catalog/products/sport/FUTBOL"))
                .andExpect(status().isOk());
    }

    // ─── ADMIN endpoints ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /products/all - ADMIN obtiene todos los productos")
    @WithMockUser(roles = "ADMIN")
    void getAllProducts_admin_200() throws Exception {
        when(productUseCase.getAllProducts()).thenReturn(List.of(product));
        when(productMapper.toResponseDTO(any())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/sportshop/catalog/products/all"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /products/all - USER no puede ver todos los productos")
    @WithMockUser(roles = "USER")
    void getAllProducts_user_403() throws Exception {
        mockMvc.perform(get("/api/sportshop/catalog/products/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /products - ADMIN crea producto")
    @WithMockUser(roles = "ADMIN")
    void createProduct_admin_201() throws Exception {
        when(productUseCase.createProduct(any(), any())).thenReturn(product);
        when(productMapper.fromRequestDTO(any())).thenReturn(product);
        when(productMapper.toResponseDTO(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/sportshop/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Zapatillas Nike"));
    }

    @Test
    @DisplayName("POST /products - USER no puede crear productos")
    @WithMockUser(roles = "USER")
    void createProduct_user_403() throws Exception {
        mockMvc.perform(post("/api/sportshop/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /products - sin autenticación retorna 401")
    void createProduct_noAuth_401() throws Exception {
        mockMvc.perform(post("/api/sportshop/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /products/{id} - ADMIN actualiza producto")
    @WithMockUser(roles = "ADMIN")
    void updateProduct_admin_200() throws Exception {
        when(productUseCase.updateProduct(eq(1L), any(), any())).thenReturn(product);
        when(productMapper.fromRequestDTO(any())).thenReturn(product);
        when(productMapper.toResponseDTO(any())).thenReturn(responseDTO);

        mockMvc.perform(put("/api/sportshop/catalog/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /products/{id} - ADMIN elimina producto")
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_admin_200() throws Exception {
        doNothing().when(productUseCase).deleteProduct(eq(1L), any());

        mockMvc.perform(delete("/api/sportshop/catalog/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Producto 1 eliminado correctamente"));
    }

    @Test
    @DisplayName("DELETE /products/{id} - USER no puede eliminar")
    @WithMockUser(roles = "USER")
    void deleteProduct_user_403() throws Exception {
        mockMvc.perform(delete("/api/sportshop/catalog/products/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /products - validación falla con cuerpo inválido")
    @WithMockUser(roles = "ADMIN")
    void createProduct_invalidBody_400() throws Exception {
        ProductRequestDTO invalid = new ProductRequestDTO();
        // missing required fields

        mockMvc.perform(post("/api/sportshop/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
