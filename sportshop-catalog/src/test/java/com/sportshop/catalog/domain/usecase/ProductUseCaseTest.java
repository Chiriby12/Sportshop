package com.sportshop.catalog.domain.usecase;

import com.sportshop.catalog.domain.model.Product;
import com.sportshop.catalog.domain.model.event.CatalogEvent;
import com.sportshop.catalog.domain.model.gateway.EventPublisherGateway;
import com.sportshop.catalog.domain.model.gateway.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductUseCase - Pruebas del dominio")
class ProductUseCaseTest {

    @Mock private ProductGateway productGateway;
    @Mock private EventPublisherGateway eventPublisher;

    private ProductUseCase productUseCase;
    private Product validProduct;

    @BeforeEach
    void setUp() {
        productUseCase = new ProductUseCase(productGateway, eventPublisher);
        // Fix: agregar null como segundo argumento (adminId)
        validProduct = new Product(1L, null, "Zapatillas Nike", "Para correr", "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("150.00"), 10, "http://img.com", true);
    }

    @Test
    @DisplayName("createProduct: debe crear producto correctamente y publicar evento")
    void createProduct_success() {
        when(productGateway.save(any())).thenReturn(validProduct);

        Product result = productUseCase.createProduct(validProduct, "ADMIN001");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Zapatillas Nike");
        verify(productGateway).save(any());

        ArgumentCaptor<CatalogEvent> eventCaptor = ArgumentCaptor.forClass(CatalogEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getType()).isEqualTo(CatalogEvent.EventType.PRODUCT_CREATED);
    }

    @Test
    @DisplayName("createProduct: debe asignar active=true si no se especifica")
    void createProduct_setsActiveTrueByDefault() {
        validProduct.setActive(null);
        when(productGateway.save(any())).thenReturn(validProduct);

        productUseCase.createProduct(validProduct, "ADMIN001");

        assertThat(validProduct.getActive()).isTrue();
    }

    @Test
    @DisplayName("createProduct: debe fallar si el nombre está vacío")
    void createProduct_emptyName_throwsException() {
        validProduct.setName("");
        assertThatThrownBy(() -> productUseCase.createProduct(validProduct, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nombre");
        verify(productGateway, never()).save(any());
    }

    @Test
    @DisplayName("createProduct: debe fallar si el nombre es null")
    void createProduct_nullName_throwsException() {
        validProduct.setName(null);
        assertThatThrownBy(() -> productUseCase.createProduct(validProduct, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    @DisplayName("createProduct: debe fallar si el precio es 0 o negativo")
    void createProduct_invalidPrice_throwsException() {
        validProduct.setPrice(BigDecimal.ZERO);
        assertThatThrownBy(() -> productUseCase.createProduct(validProduct, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("precio");
    }

    @Test
    @DisplayName("createProduct: debe fallar si el precio es null")
    void createProduct_nullPrice_throwsException() {
        validProduct.setPrice(null);
        assertThatThrownBy(() -> productUseCase.createProduct(validProduct, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("precio");
    }

    @Test
    @DisplayName("createProduct: debe fallar si el stock es negativo")
    void createProduct_negativeStock_throwsException() {
        validProduct.setStock(-1);
        assertThatThrownBy(() -> productUseCase.createProduct(validProduct, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("stock");
    }

    @Test
    @DisplayName("createProduct: debe fallar si el stock es null")
    void createProduct_nullStock_throwsException() {
        validProduct.setStock(null);
        assertThatThrownBy(() -> productUseCase.createProduct(validProduct, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("stock");
    }

    @Test
    @DisplayName("getProductById: debe retornar el producto si existe")
    void getProductById_found() {
        when(productGateway.findById(1L)).thenReturn(Optional.of(validProduct));

        Product result = productUseCase.getProductById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Zapatillas Nike");
    }

    @Test
    @DisplayName("getProductById: debe lanzar excepción si no existe")
    void getProductById_notFound() {
        when(productGateway.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productUseCase.getProductById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("getAllProducts: debe retornar lista completa")
    void getAllProducts_returnsList() {
        when(productGateway.findAll()).thenReturn(List.of(validProduct));

        List<Product> results = productUseCase.getAllProducts();

        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("getActiveProducts: debe retornar solo activos")
    void getActiveProducts_returnsActive() {
        when(productGateway.findActive()).thenReturn(List.of(validProduct));

        List<Product> results = productUseCase.getActiveProducts();

        assertThat(results).allMatch(Product::getActive);
    }

    @Test
    @DisplayName("getProductsByCategory: debe retornar productos de la categoría")
    void getProductsByCategory_success() {
        when(productGateway.findByCategory("RUNNING")).thenReturn(List.of(validProduct));

        List<Product> results = productUseCase.getProductsByCategory("RUNNING");

        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("getProductsByCategory: debe fallar con categoría vacía")
    void getProductsByCategory_emptyCategory() {
        assertThatThrownBy(() -> productUseCase.getProductsByCategory(""))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("categoría");
    }

    @Test
    @DisplayName("getProductsByCategory: debe fallar con categoría null")
    void getProductsByCategory_nullCategory() {
        assertThatThrownBy(() -> productUseCase.getProductsByCategory(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("getProductsBySport: debe retornar productos del deporte")
    void getProductsBySport_success() {
        when(productGateway.findBySport("ATLETISMO")).thenReturn(List.of(validProduct));

        List<Product> results = productUseCase.getProductsBySport("ATLETISMO");

        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("getProductsBySport: debe fallar con deporte vacío")
    void getProductsBySport_empty() {
        assertThatThrownBy(() -> productUseCase.getProductsBySport(""))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("deporte");
    }

    @Test
    @DisplayName("getProductsBySport: debe fallar con deporte null")
    void getProductsBySport_null() {
        assertThatThrownBy(() -> productUseCase.getProductsBySport(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("updateProduct: debe actualizar correctamente y publicar evento")
    void updateProduct_success() {
        when(productGateway.existsById(1L)).thenReturn(true);
        when(productGateway.save(any())).thenReturn(validProduct);

        Product result = productUseCase.updateProduct(1L, validProduct, "ADMIN001");

        assertThat(result).isNotNull();
        verify(eventPublisher).publish(argThat(e -> e.getType() == CatalogEvent.EventType.PRODUCT_UPDATED));
    }

    @Test
    @DisplayName("updateProduct: debe fallar si el producto no existe")
    void updateProduct_notFound() {
        when(productGateway.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productUseCase.updateProduct(99L, validProduct, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
        verify(productGateway, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct: debe fallar si el precio es inválido")
    void updateProduct_invalidPrice() {
        when(productGateway.existsById(1L)).thenReturn(true);
        validProduct.setPrice(BigDecimal.valueOf(-5));

        assertThatThrownBy(() -> productUseCase.updateProduct(1L, validProduct, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("precio");
    }

    @Test
    @DisplayName("updateProduct: debe fallar si el stock es negativo")
    void updateProduct_invalidStock() {
        when(productGateway.existsById(1L)).thenReturn(true);
        validProduct.setStock(-3);

        assertThatThrownBy(() -> productUseCase.updateProduct(1L, validProduct, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("stock");
    }

    @Test
    @DisplayName("updateProduct: precio null y stock null deben ser permitidos en update")
    void updateProduct_nullPriceAndStock_allowed() {
        when(productGateway.existsById(1L)).thenReturn(true);
        validProduct.setPrice(null);
        validProduct.setStock(null);
        when(productGateway.save(any())).thenReturn(validProduct);

        assertThatCode(() -> productUseCase.updateProduct(1L, validProduct, "ADMIN001"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteProduct: debe eliminar y publicar evento")
    void deleteProduct_success() {
        when(productGateway.findById(1L)).thenReturn(Optional.of(validProduct));

        productUseCase.deleteProduct(1L, "ADMIN001");

        verify(productGateway).deleteById(1L);
        verify(eventPublisher).publish(argThat(e -> e.getType() == CatalogEvent.EventType.PRODUCT_DELETED));
    }

    @Test
    @DisplayName("deleteProduct: debe fallar si el producto no existe")
    void deleteProduct_notFound() {
        when(productGateway.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productUseCase.deleteProduct(99L, "ADMIN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
        verify(productGateway, never()).deleteById(any());
    }
}