package com.sportshop.admin.domain.usecase;

import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.domain.model.event.AdminEvent;
import com.sportshop.admin.domain.model.gateway.AdminProductGateway;
import com.sportshop.admin.domain.model.gateway.CatalogSyncGateway;
import com.sportshop.admin.domain.model.gateway.EventPublisherGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProductUseCaseTest {

    @Mock private AdminProductGateway productGateway;
    @Mock private EventPublisherGateway eventPublisher;
    @Mock private CatalogSyncGateway catalogSync;

    private AdminProductUseCase useCase;

    private AdminProduct product;

    @BeforeEach
    void setUp() {
        useCase = new AdminProductUseCase(productGateway, eventPublisher, catalogSync);
        product = new AdminProduct(null, "Camiseta Nike", "Camiseta deportiva",
                "Nike", "RUNNING", "ATLETISMO",
                new BigDecimal("85000"), 50, "https://img.com/cam.jpg", true);
    }

    // ══════════ createProduct ══════════

    @Test
    @DisplayName("createProduct: crea correctamente")
    void createProduct_exitoso() {
        AdminProduct saved = new AdminProduct(1L, "Camiseta Nike", "Camiseta deportiva",
                "Nike", "RUNNING", "ATLETISMO", new BigDecimal("85000"), 50, null, true);
        when(productGateway.save(any())).thenReturn(saved);
        doNothing().when(catalogSync).createOrUpdate(any());

        AdminProduct result = useCase.createProduct(product, "admin-doc");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventPublisher).publish(any(AdminEvent.class));
        verify(catalogSync).createOrUpdate(any());
    }

    @Test
    @DisplayName("createProduct: asigna active=true por defecto")
    void createProduct_activePorDefecto() {
        product.setActive(null);
        AdminProduct saved = new AdminProduct(1L, "Camiseta Nike", null, "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("85000"), 50, null, true);
        when(productGateway.save(any())).thenReturn(saved);
        doNothing().when(catalogSync).createOrUpdate(any());

        useCase.createProduct(product, "admin");
        assertTrue(product.getActive());
    }

    @Test
    @DisplayName("createProduct: error si nombre vacío")
    void createProduct_nombreVacio() {
        product.setName("  ");
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.createProduct(product, "admin"));
        assertEquals("El nombre del producto no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("createProduct: error si nombre null")
    void createProduct_nombreNull() {
        product.setName(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.createProduct(product, "admin"));
        assertEquals("El nombre del producto no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("createProduct: error si precio null")
    void createProduct_precioNull() {
        product.setPrice(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.createProduct(product, "admin"));
        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }

    @Test
    @DisplayName("createProduct: error si precio cero")
    void createProduct_precioCero() {
        product.setPrice(BigDecimal.ZERO);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.createProduct(product, "admin"));
        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }

    @Test
    @DisplayName("createProduct: error si precio negativo")
    void createProduct_precioNegativo() {
        product.setPrice(new BigDecimal("-100"));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.createProduct(product, "admin"));
        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }

    @Test
    @DisplayName("createProduct: error si stock null")
    void createProduct_stockNull() {
        product.setStock(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.createProduct(product, "admin"));
        assertEquals("El stock no puede ser negativo", ex.getMessage());
    }

    @Test
    @DisplayName("createProduct: error si stock negativo")
    void createProduct_stockNegativo() {
        product.setStock(-1);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.createProduct(product, "admin"));
        assertEquals("El stock no puede ser negativo", ex.getMessage());
    }

    @Test
    @DisplayName("createProduct: error si marca vacía")
    void createProduct_marcaVacia() {
        product.setBrand("  ");
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.createProduct(product, "admin"));
        assertEquals("La marca no puede estar vacía", ex.getMessage());
    }

    // ══════════ getProductById ══════════

    @Test
    @DisplayName("getProductById: retorna producto existente")
    void getProductById_exitoso() {
        product.setId(1L);
        when(productGateway.findById(1L)).thenReturn(Optional.of(product));
        AdminProduct result = useCase.getProductById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getProductById: error si no existe")
    void getProductById_noExiste() {
        when(productGateway.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.getProductById(99L));
        assertTrue(ex.getMessage().contains("No existe un producto con id: 99"));
    }

    @Test
    @DisplayName("getProductById: error si ID null")
    void getProductById_idNull() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.getProductById(null));
        assertEquals("El ID debe ser un número positivo", ex.getMessage());
    }

    @Test
    @DisplayName("getProductById: error si ID cero")
    void getProductById_idCero() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.getProductById(0L));
        assertEquals("El ID debe ser un número positivo", ex.getMessage());
    }

    // ══════════ getAllProducts ══════════

    @Test
    @DisplayName("getAllProducts: retorna lista")
    void getAllProducts_exitoso() {
        when(productGateway.findAll()).thenReturn(List.of(product));
        List<AdminProduct> result = useCase.getAllProducts();
        assertEquals(1, result.size());
    }

    // ══════════ getActiveProducts ══════════

    @Test
    @DisplayName("getActiveProducts: retorna solo activos")
    void getActiveProducts_exitoso() {
        when(productGateway.findActive()).thenReturn(List.of(product));
        List<AdminProduct> result = useCase.getActiveProducts();
        assertFalse(result.isEmpty());
    }

    // ══════════ getProductsByCategory ══════════

    @Test
    @DisplayName("getProductsByCategory: filtra correctamente")
    void getProductsByCategory_exitoso() {
        when(productGateway.findByCategory("RUNNING")).thenReturn(List.of(product));
        List<AdminProduct> result = useCase.getProductsByCategory("RUNNING");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getProductsByCategory: error si categoría vacía")
    void getProductsByCategory_vacia() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.getProductsByCategory("  "));
        assertEquals("La categoría no puede estar vacía", ex.getMessage());
    }

    // ══════════ getProductsBySport ══════════

    @Test
    @DisplayName("getProductsBySport: filtra correctamente")
    void getProductsBySport_exitoso() {
        when(productGateway.findBySport("ATLETISMO")).thenReturn(List.of(product));
        List<AdminProduct> result = useCase.getProductsBySport("ATLETISMO");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getProductsBySport: error si deporte vacío")
    void getProductsBySport_vacio() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.getProductsBySport(""));
        assertEquals("El deporte no puede estar vacío", ex.getMessage());
    }

    // ══════════ updateProduct ══════════

    @Test
    @DisplayName("updateProduct: actualiza correctamente")
    void updateProduct_exitoso() {
        product.setId(1L);
        when(productGateway.existsById(1L)).thenReturn(true);
        when(productGateway.save(any())).thenReturn(product);
        doNothing().when(catalogSync).createOrUpdate(any());

        AdminProduct result = useCase.updateProduct(1L, product, "admin-doc");

        assertNotNull(result);
        verify(eventPublisher).publish(any(AdminEvent.class));
        verify(catalogSync).createOrUpdate(any());
    }

    @Test
    @DisplayName("updateProduct: error si no existe")
    void updateProduct_noExiste() {
        when(productGateway.existsById(99L)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.updateProduct(99L, product, "admin"));
        assertTrue(ex.getMessage().contains("No existe un producto con id: 99"));
    }

    @Test
    @DisplayName("updateProduct: error si precio inválido en update")
    void updateProduct_precioInvalido() {
        when(productGateway.existsById(1L)).thenReturn(true);
        product.setPrice(new BigDecimal("-50"));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.updateProduct(1L, product, "admin"));
        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }

    @Test
    @DisplayName("updateProduct: error si stock negativo en update")
    void updateProduct_stockNegativo() {
        when(productGateway.existsById(1L)).thenReturn(true);
        product.setStock(-10);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.updateProduct(1L, product, "admin"));
        assertEquals("El stock no puede ser negativo", ex.getMessage());
    }

    // ══════════ deleteProduct ══════════

    @Test
    @DisplayName("deleteProduct: elimina correctamente")
    void deleteProduct_exitoso() {
        product.setId(1L);
        when(productGateway.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productGateway).deleteById(1L);
        doNothing().when(catalogSync).delete(1L);

        assertDoesNotThrow(() -> useCase.deleteProduct(1L, "admin-doc"));
        verify(productGateway).deleteById(1L);
        verify(eventPublisher).publish(any(AdminEvent.class));
        verify(catalogSync).delete(1L);
    }

    @Test
    @DisplayName("deleteProduct: error si no existe")
    void deleteProduct_noExiste() {
        when(productGateway.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.deleteProduct(99L, "admin"));
        assertTrue(ex.getMessage().contains("No existe un producto con id: 99"));
        verify(productGateway, never()).deleteById(any());
    }
}