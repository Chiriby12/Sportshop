package com.sportshop.catalog.infraestructure.entry_points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshop.catalog.application.config.OpenApiConfig;
import com.sportshop.catalog.application.config.SecurityConfig;
import com.sportshop.catalog.application.dto.CartRequestDTO;
import com.sportshop.catalog.application.dto.CartResponseDTO;
import com.sportshop.catalog.domain.model.CartItem;
import com.sportshop.catalog.domain.usecase.CartUseCase;
import com.sportshop.catalog.infraestructure.mapper.CartMapper;
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

@WebMvcTest(CartController.class)
@Import({SecurityConfig.class, OpenApiConfig.class, JwtFilter.class})
@DisplayName("CartController - Tests de integración HTTP")
class CartControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private CartUseCase cartUseCase;
    @MockitoBean private CartMapper cartMapper;

    private CartItem cartItem;
    private CartResponseDTO cartResponseDTO;

    @BeforeEach
    void setUp() {
        cartItem = new CartItem(1L, "USR123", 1L, "Guayos Adidas", new BigDecimal("200.00"), 2);
        cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setId(1L);
        cartResponseDTO.setProductName("Guayos Adidas");
        cartResponseDTO.setQuantity(2);
        cartResponseDTO.setUnitPrice(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("GET /cart - usuario autenticado ve su carrito")
    @WithMockUser(roles = "USER")
    void getMyCart_user_200() throws Exception {
        when(cartUseCase.getCart(any())).thenReturn(List.of(cartItem));
        when(cartMapper.toResponseDTO(any())).thenReturn(cartResponseDTO);

        mockMvc.perform(get("/api/sportshop/catalog/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].productName").value("Guayos Adidas"));
    }

    @Test
    @DisplayName("GET /cart - sin autenticación retorna 401")
    void getMyCart_noAuth_401() throws Exception {
        mockMvc.perform(get("/api/sportshop/catalog/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /cart - usuario agrega producto al carrito")
    @WithMockUser(roles = "USER")
    void addToCart_user_201() throws Exception {
        CartRequestDTO dto = new CartRequestDTO(1L, 2);
        when(cartUseCase.addToCart(any(), eq(1L), eq(2))).thenReturn(cartItem);
        when(cartMapper.toResponseDTO(any())).thenReturn(cartResponseDTO);

        mockMvc.perform(post("/api/sportshop/catalog/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /cart - validación falla con body inválido")
    @WithMockUser(roles = "USER")
    void addToCart_invalidBody_400() throws Exception {
        CartRequestDTO invalid = new CartRequestDTO();

        mockMvc.perform(post("/api/sportshop/catalog/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /cart/{id} - actualiza cantidad de un ítem")
    @WithMockUser(roles = "USER")
    void updateCartItem_user_200() throws Exception {
        when(cartUseCase.updateCartItem(eq(1L), eq(3), any())).thenReturn(cartItem);
        when(cartMapper.toResponseDTO(any())).thenReturn(cartResponseDTO);

        mockMvc.perform(put("/api/sportshop/catalog/cart/1")
                        .param("quantity", "3"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /cart/{id} - elimina ítem del carrito")
    @WithMockUser(roles = "USER")
    void removeFromCart_user_200() throws Exception {
        doNothing().when(cartUseCase).removeFromCart(eq(1L), any());

        mockMvc.perform(delete("/api/sportshop/catalog/cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Ítem removido del carrito"));
    }

    @Test
    @DisplayName("POST /cart/purchase - compra el carrito completo")
    @WithMockUser(roles = "USER")
    void purchase_user_200() throws Exception {
        doNothing().when(cartUseCase).purchaseCart(any());

        mockMvc.perform(post("/api/sportshop/catalog/cart/purchase"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("¡Compra realizada exitosamente!"));
    }

    @Test
    @DisplayName("POST /cart/purchase - sin auth retorna 401")
    void purchase_noAuth_401() throws Exception {
        mockMvc.perform(post("/api/sportshop/catalog/cart/purchase"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /cart - ADMIN también puede ver el carrito")
    @WithMockUser(roles = "ADMIN")
    void getMyCart_admin_200() throws Exception {
        when(cartUseCase.getCart(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/sportshop/catalog/cart"))
                .andExpect(status().isOk());
    }
}
