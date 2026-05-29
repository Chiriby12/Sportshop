package com.sportshop.catalog.domain.usecase;

import com.sportshop.catalog.domain.model.CartItem;
import com.sportshop.catalog.domain.model.Product;
import com.sportshop.catalog.domain.model.event.CatalogEvent;
import com.sportshop.catalog.domain.model.gateway.CartGateway;
import com.sportshop.catalog.domain.model.gateway.EventPublisherGateway;
import com.sportshop.catalog.domain.model.gateway.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartUseCase - Pruebas del dominio del carrito")
class CartUseCaseTest {

    @Mock private CartGateway cartGateway;
    @Mock private ProductGateway productGateway;
    @Mock private EventPublisherGateway eventPublisher;

    private CartUseCase cartUseCase;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        cartUseCase = new CartUseCase(cartGateway, productGateway, eventPublisher);

        product = new Product(1L, null, "Guayos Adidas", "Para fútbol", "Adidas",
                "FOOTBALL", "FUTBOL", new BigDecimal("200.00"), 5, null, true);
        cartItem = new CartItem(1L, "USR123", 1L, "Guayos Adidas", new BigDecimal("200.00"), 2);
    }

    @Test
    @DisplayName("addToCart: debe agregar ítem y publicar evento")
    void addToCart_success() {
        when(productGateway.findById(1L)).thenReturn(Optional.of(product));
        when(cartGateway.save(any())).thenReturn(cartItem);

        CartItem result = cartUseCase.addToCart("USR123", 1L, 2);

        assertThat(result).isNotNull();
        verify(cartGateway).save(any());
        verify(eventPublisher).publish(argThat(e -> e.getType() == CatalogEvent.EventType.CART_ITEM_ADDED));
    }

    @Test
    @DisplayName("addToCart: debe fallar si documento es vacío")
    void addToCart_emptyDocument() {
        assertThatThrownBy(() -> cartUseCase.addToCart("", 1L, 2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("documento");
    }

    @Test
    @DisplayName("addToCart: debe fallar si documento es null")
    void addToCart_nullDocument() {
        assertThatThrownBy(() -> cartUseCase.addToCart(null, 1L, 2))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("addToCart: debe fallar si cantidad es 0")
    void addToCart_zeroQuantity() {
        assertThatThrownBy(() -> cartUseCase.addToCart("USR123", 1L, 0))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cantidad");
    }

    @Test
    @DisplayName("addToCart: debe fallar si cantidad es null")
    void addToCart_nullQuantity() {
        assertThatThrownBy(() -> cartUseCase.addToCart("USR123", 1L, null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("addToCart: debe fallar si producto no existe")
    void addToCart_productNotFound() {
        when(productGateway.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartUseCase.addToCart("USR123", 99L, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("addToCart: debe fallar si producto está inactivo")
    void addToCart_inactiveProduct() {
        product.setActive(false);
        when(productGateway.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartUseCase.addToCart("USR123", 1L, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("disponible");
    }

    @Test
    @DisplayName("addToCart: debe fallar si no hay suficiente stock")
    void addToCart_insufficientStock() {
        product.setStock(1);
        when(productGateway.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartUseCase.addToCart("USR123", 1L, 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock");
    }

    @Test
    @DisplayName("getCart: debe retornar los ítems del usuario")
    void getCart_success() {
        when(cartGateway.findByUserDocument("USR123")).thenReturn(List.of(cartItem));

        List<CartItem> result = cartUseCase.getCart("USR123");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getCart: debe fallar si documento es vacío")
    void getCart_emptyDocument() {
        assertThatThrownBy(() -> cartUseCase.getCart(""))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("documento");
    }

    @Test
    @DisplayName("getCart: debe fallar si documento es null")
    void getCart_nullDocument() {
        assertThatThrownBy(() -> cartUseCase.getCart(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("updateCartItem: debe actualizar cantidad y publicar evento")
    void updateCartItem_success() {
        when(cartGateway.findById(1L)).thenReturn(Optional.of(cartItem));
        when(productGateway.findById(1L)).thenReturn(Optional.of(product));
        when(cartGateway.save(any())).thenReturn(cartItem);

        CartItem result = cartUseCase.updateCartItem(1L, 3, "USR123");

        assertThat(result).isNotNull();
        verify(eventPublisher).publish(argThat(e -> e.getType() == CatalogEvent.EventType.CART_ITEM_UPDATED));
    }

    @Test
    @DisplayName("updateCartItem: debe fallar si cantidad es 0")
    void updateCartItem_zeroQuantity() {
        assertThatThrownBy(() -> cartUseCase.updateCartItem(1L, 0, "USR123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cantidad");
    }

    @Test
    @DisplayName("updateCartItem: debe fallar si ítem no existe")
    void updateCartItem_itemNotFound() {
        when(cartGateway.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartUseCase.updateCartItem(99L, 2, "USR123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("updateCartItem: debe fallar si el ítem pertenece a otro usuario")
    void updateCartItem_wrongUser() {
        when(cartGateway.findById(1L)).thenReturn(Optional.of(cartItem));

        assertThatThrownBy(() -> cartUseCase.updateCartItem(1L, 2, "OTRO_USER"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("permiso");
    }

    @Test
    @DisplayName("updateCartItem: debe fallar si stock insuficiente")
    void updateCartItem_insufficientStock() {
        product.setStock(1);
        when(cartGateway.findById(1L)).thenReturn(Optional.of(cartItem));
        when(productGateway.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartUseCase.updateCartItem(1L, 5, "USR123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock");
    }

    @Test
    @DisplayName("updateCartItem: debe fallar si producto asociado ya no existe")
    void updateCartItem_productGone() {
        when(cartGateway.findById(1L)).thenReturn(Optional.of(cartItem));
        when(productGateway.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartUseCase.updateCartItem(1L, 2, "USR123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya no existe");
    }

    @Test
    @DisplayName("removeFromCart: debe eliminar y publicar evento")
    void removeFromCart_success() {
        when(cartGateway.findById(1L)).thenReturn(Optional.of(cartItem));

        cartUseCase.removeFromCart(1L, "USR123");

        verify(cartGateway).deleteById(1L);
        verify(eventPublisher).publish(argThat(e -> e.getType() == CatalogEvent.EventType.CART_ITEM_REMOVED));
    }

    @Test
    @DisplayName("removeFromCart: debe fallar si ítem no existe")
    void removeFromCart_notFound() {
        when(cartGateway.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartUseCase.removeFromCart(99L, "USR123"))
                .isInstanceOf(RuntimeException.class);
        verify(cartGateway, never()).deleteById(any());
    }

    @Test
    @DisplayName("removeFromCart: debe fallar si el ítem no es del usuario")
    void removeFromCart_wrongUser() {
        when(cartGateway.findById(1L)).thenReturn(Optional.of(cartItem));

        assertThatThrownBy(() -> cartUseCase.removeFromCart(1L, "OTRO"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("permiso");
    }

    @Test
    @DisplayName("purchaseCart: debe comprar, descontar stock y publicar evento")
    void purchaseCart_success() {
        when(cartGateway.findByUserDocument("USR123")).thenReturn(List.of(cartItem));
        when(productGateway.findById(1L)).thenReturn(Optional.of(product));
        when(productGateway.save(any())).thenReturn(product);

        cartUseCase.purchaseCart("USR123");

        verify(productGateway, times(2)).findById(1L);
        verify(productGateway).save(argThat(p -> p.getStock() == 3));
        verify(cartGateway).deleteAllByUserDocument("USR123");
        verify(eventPublisher).publish(argThat(e -> e.getType() == CatalogEvent.EventType.CART_PURCHASED));
    }

    @Test
    @DisplayName("purchaseCart: debe fallar si carrito está vacío")
    void purchaseCart_emptyCart() {
        when(cartGateway.findByUserDocument("USR123")).thenReturn(List.of());

        assertThatThrownBy(() -> cartUseCase.purchaseCart("USR123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("vacío");
    }

    @Test
    @DisplayName("purchaseCart: debe fallar si no hay stock suficiente al comprar")
    void purchaseCart_insufficientStockAtPurchase() {
        product.setStock(1);
        when(cartGateway.findByUserDocument("USR123")).thenReturn(List.of(cartItem));
        when(productGateway.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartUseCase.purchaseCart("USR123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("insuficiente");
    }

    @Test
    @DisplayName("purchaseCart: debe fallar si el producto ya no existe al comprar")
    void purchaseCart_productGone() {
        when(cartGateway.findByUserDocument("USR123")).thenReturn(List.of(cartItem));
        when(productGateway.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartUseCase.purchaseCart("USR123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya no existe");
    }
}