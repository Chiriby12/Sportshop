package com.sportshop.catalog.infraestructure.mapper;

import com.sportshop.catalog.application.dto.CartResponseDTO;
import com.sportshop.catalog.domain.model.CartItem;
import com.sportshop.catalog.infraestructure.driver_adapters.jpa_repository.CartItemData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CartMapper - Tests de mapeo del carrito")
class CartMapperTest {

    private CartMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CartMapper();
    }

    @Test
    @DisplayName("toDomain: mapea CartItemData a CartItem")
    void toDomain_success() {
        CartItemData data = new CartItemData(1L, "USR123", 2L, "Guayos", new BigDecimal("200"), 3);

        CartItem result = mapper.toDomain(data);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserDocument()).isEqualTo("USR123");
        assertThat(result.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("toDomain: retorna null si data es null")
    void toDomain_null() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toData: mapea CartItem a CartItemData")
    void toData_success() {
        CartItem item = new CartItem(1L, "USR123", 2L, "Guayos", new BigDecimal("200"), 3);

        CartItemData result = mapper.toData(item);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserDocument()).isEqualTo("USR123");
    }

    @Test
    @DisplayName("toData: retorna null si domain es null")
    void toData_null() {
        assertThat(mapper.toData(null)).isNull();
    }

    @Test
    @DisplayName("toResponseDTO: mapea CartItem a CartResponseDTO con subtotal calculado")
    void toResponseDTO_success() {
        CartItem item = new CartItem(1L, "USR123", 2L, "Guayos", new BigDecimal("200.00"), 3);

        CartResponseDTO result = mapper.toResponseDTO(item);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("Guayos");
        assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("600.00"));
    }

    @Test
    @DisplayName("toResponseDTO: retorna null si domain es null")
    void toResponseDTO_null() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }

    @Test
    @DisplayName("CartResponseDTO.getSubtotal: retorna ZERO si unitPrice es null")
    void cartResponseDTO_subtotal_nullUnitPrice() {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setQuantity(3);

        assertThat(dto.getSubtotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("CartResponseDTO.getSubtotal: retorna ZERO si quantity es null")
    void cartResponseDTO_subtotal_nullQuantity() {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setUnitPrice(new BigDecimal("100"));

        assertThat(dto.getSubtotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
