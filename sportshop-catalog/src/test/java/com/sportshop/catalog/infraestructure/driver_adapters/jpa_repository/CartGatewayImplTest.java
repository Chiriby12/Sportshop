package com.sportshop.catalog.infraestructure.driver_adapters.jpa_repository;

import com.sportshop.catalog.domain.model.CartItem;
import com.sportshop.catalog.infraestructure.mapper.CartMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartGatewayImpl - Tests del adaptador JPA del carrito")
class CartGatewayImplTest {

    @Mock private CartItemJpaRepository jpaRepository;
    @Mock private CartMapper mapper;
    @InjectMocks private CartGatewayImpl gateway;

    private CartItem cartItem;
    private CartItemData cartItemData;

    @BeforeEach
    void setUp() {
        cartItem = new CartItem(1L, "12345678", 1L, "Camiseta Nike",
                new BigDecimal("85000"), 2);
        cartItemData = new CartItemData(1L, "12345678", 1L, "Camiseta Nike",
                new BigDecimal("85000"), 2);
    }

    @Test
    @DisplayName("save: guarda y retorna dominio mapeado")
    void save_ok() {
        when(mapper.toData(cartItem)).thenReturn(cartItemData);
        when(jpaRepository.save(cartItemData)).thenReturn(cartItemData);
        when(mapper.toDomain(cartItemData)).thenReturn(cartItem);

        CartItem result = gateway.save(cartItem);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("findById: retorna Optional con el ítem cuando existe")
    void findById_existe() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(cartItemData));
        when(mapper.toDomain(cartItemData)).thenReturn(cartItem);

        Optional<CartItem> result = gateway.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("findById: retorna Optional vacío cuando no existe")
    void findById_noExiste() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CartItem> result = gateway.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByUserDocument: retorna ítems del carrito de un usuario")
    void findByUserDocument_ok() {
        when(jpaRepository.findByUserDocument("12345678")).thenReturn(List.of(cartItemData));
        when(mapper.toDomain(cartItemData)).thenReturn(cartItem);

        List<CartItem> result = gateway.findByUserDocument("12345678");

        assertEquals(1, result.size());
        assertEquals("12345678", result.get(0).getUserDocument());
    }

    @Test
    @DisplayName("deleteById: elimina el ítem por ID")
    void deleteById_ok() {
        doNothing().when(jpaRepository).deleteById(1L);
        gateway.deleteById(1L);
        verify(jpaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteAllByUserDocument: vacía el carrito de un usuario")
    void deleteAllByUserDocument_ok() {
        doNothing().when(jpaRepository).deleteAllByUserDocument("12345678");
        gateway.deleteAllByUserDocument("12345678");
        verify(jpaRepository).deleteAllByUserDocument("12345678");
    }
}
