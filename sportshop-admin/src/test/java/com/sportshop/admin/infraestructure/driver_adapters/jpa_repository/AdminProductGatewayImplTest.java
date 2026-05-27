package com.sportshop.admin.infraestructure.driver_adapters.jpa_repository;

import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.infraestructure.mapper.AdminProductMapper;
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
@DisplayName("AdminProductGatewayImpl - Tests del adaptador JPA de productos admin")
class AdminProductGatewayImplTest {

    @Mock private AdminProductJpaRepository jpaRepository;
    @Mock private AdminProductMapper mapper;
    @InjectMocks private AdminProductGatewayImpl gateway;

    private AdminProduct product;
    private AdminProductData productData;

    @BeforeEach
    void setUp() {
        product = new AdminProduct(1L, "Camiseta", "desc", "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("85000"), 50, "url", true);
        productData = new AdminProductData(1L, "Camiseta", "desc", "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("85000"), 50, "url", true);
    }

    @Test
    @DisplayName("save: guarda y retorna dominio mapeado")
    void save_ok() {
        when(mapper.toData(product)).thenReturn(productData);
        when(jpaRepository.save(productData)).thenReturn(productData);
        when(mapper.toDomain(productData)).thenReturn(product);

        AdminProduct result = gateway.save(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("findById: retorna Optional con producto cuando existe")
    void findById_existe() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(productData));
        when(mapper.toDomain(productData)).thenReturn(product);

        Optional<AdminProduct> result = gateway.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Camiseta", result.get().getName());
    }

    @Test
    @DisplayName("findById: retorna Optional vacío cuando no existe")
    void findById_noExiste() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<AdminProduct> result = gateway.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findAll: retorna lista completa mapeada")
    void findAll_ok() {
        when(jpaRepository.findAll()).thenReturn(List.of(productData));
        when(mapper.toDomain(productData)).thenReturn(product);

        List<AdminProduct> result = gateway.findAll();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findByCategory: filtra por categoría correctamente")
    void findByCategory_ok() {
        when(jpaRepository.findByCategory("RUNNING")).thenReturn(List.of(productData));
        when(mapper.toDomain(productData)).thenReturn(product);

        List<AdminProduct> result = gateway.findByCategory("RUNNING");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findBySport: filtra por deporte correctamente")
    void findBySport_ok() {
        when(jpaRepository.findBySport("ATLETISMO")).thenReturn(List.of(productData));
        when(mapper.toDomain(productData)).thenReturn(product);

        List<AdminProduct> result = gateway.findBySport("ATLETISMO");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findActive: retorna solo productos activos")
    void findActive_ok() {
        when(jpaRepository.findByActiveTrue()).thenReturn(List.of(productData));
        when(mapper.toDomain(productData)).thenReturn(product);

        List<AdminProduct> result = gateway.findActive();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("deleteById: delega al repositorio correctamente")
    void deleteById_ok() {
        doNothing().when(jpaRepository).deleteById(1L);
        gateway.deleteById(1L);
        verify(jpaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("existsById: retorna true si existe")
    void existsById_true() {
        when(jpaRepository.existsById(1L)).thenReturn(true);
        assertTrue(gateway.existsById(1L));
    }

    @Test
    @DisplayName("existsById: retorna false si no existe")
    void existsById_false() {
        when(jpaRepository.existsById(99L)).thenReturn(false);
        assertFalse(gateway.existsById(99L));
    }
}
