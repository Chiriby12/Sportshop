package com.sportshop.admin.infraestructure.mapper;

import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.infraestructure.driver_adapters.jpa_repository.AdminProductData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AdminProductMapperTest {

    private final AdminProductMapper mapper = new AdminProductMapper();

    @Test
    @DisplayName("toDomain: convierte AdminProductData a AdminProduct correctamente")
    void toDomain_exitoso() {
        AdminProductData data = new AdminProductData(1L, "Camiseta", "desc",
                "Nike", "RUNNING", "ATLETISMO", new BigDecimal("85000"), 50, "url", true);

        AdminProduct domain = mapper.toDomain(data);

        assertEquals(1L, domain.getId());
        assertEquals("Camiseta", domain.getName());
        assertEquals("Nike", domain.getBrand());
        assertTrue(domain.getActive());
    }

    @Test
    @DisplayName("toDomain: retorna null si data es null")
    void toDomain_null() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("toData: convierte AdminProduct a AdminProductData correctamente")
    void toData_exitoso() {
        AdminProduct product = new AdminProduct(2L, "Tenis", "desc",
                "Adidas", "BASKETBALL", "BALONCESTO", new BigDecimal("250000"), 20, "url", false);

        AdminProductData data = mapper.toData(product);

        assertEquals(2L, data.getId());
        assertEquals("Adidas", data.getBrand());
        assertFalse(data.getActive());
    }

    @Test
    @DisplayName("toData: retorna null si domain es null")
    void toData_null() {
        assertNull(mapper.toData(null));
    }

    @Test
    @DisplayName("toData: activo=true por defecto si es null")
    void toData_activoPorDefecto() {
        AdminProduct product = new AdminProduct(null, "Pelota", null,
                "Wilson", null, null, new BigDecimal("50000"), 10, null, null);
        AdminProductData data = mapper.toData(product);
        assertTrue(data.getActive());
    }
}
