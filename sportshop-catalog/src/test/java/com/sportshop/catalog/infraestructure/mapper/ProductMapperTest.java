package com.sportshop.catalog.infraestructure.mapper;

import com.sportshop.catalog.application.dto.ProductRequestDTO;
import com.sportshop.catalog.application.dto.ProductResponseDTO;
import com.sportshop.catalog.domain.model.Product;
import com.sportshop.catalog.infraestructure.driver_adapters.jpa_repository.ProductData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ProductMapper - Tests de mapeo")
class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
    }

    @Test
    @DisplayName("toDomain: debe mapear ProductData a Product correctamente")
    void toDomain_success() {
        // Fix: agregar null como segundo argumento (adminId)
        ProductData data = new ProductData(1L, null, "Nike", "desc", "Nike", "RUNNING",
                "ATLETISMO", new BigDecimal("100"), 5, "http://img", true);

        Product result = mapper.toDomain(data);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Nike");
        assertThat(result.getActive()).isTrue();
    }

    @Test
    @DisplayName("toDomain: retorna null si data es null")
    void toDomain_null() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toData: debe mapear Product a ProductData correctamente")
    void toData_success() {
        Product p = new Product(1L, null, "Nike", "desc", "Nike", "RUNNING",
                "ATLETISMO", new BigDecimal("100"), 5, "http://img", true);

        ProductData result = mapper.toData(p);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("toData: retorna null si domain es null")
    void toData_null() {
        assertThat(mapper.toData(null)).isNull();
    }

    @Test
    @DisplayName("toData: active null por defecto se pone true")
    void toData_nullActive_defaultsToTrue() {
        Product p = new Product(1L, null, "Nike", null, null, null, null,
                new BigDecimal("100"), 5, null, null);

        ProductData result = mapper.toData(p);

        assertThat(result.getActive()).isTrue();
    }

    @Test
    @DisplayName("fromRequestDTO: mapea correctamente")
    void fromRequestDTO_success() {
        ProductRequestDTO dto = new ProductRequestDTO("Nike", "desc", "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("100"), 5, "http://img", true);

        Product result = mapper.fromRequestDTO(dto);

        assertThat(result.getName()).isEqualTo("Nike");
        assertThat(result.getActive()).isTrue();
    }

    @Test
    @DisplayName("fromRequestDTO: active null por defecto es true")
    void fromRequestDTO_nullActive_defaultsToTrue() {
        ProductRequestDTO dto = new ProductRequestDTO("Nike", null, "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("100"), 5, null, null);

        Product result = mapper.fromRequestDTO(dto);

        assertThat(result.getActive()).isTrue();
    }

    @Test
    @DisplayName("fromRequestDTO: retorna null si dto es null")
    void fromRequestDTO_null() {
        assertThat(mapper.fromRequestDTO(null)).isNull();
    }

    @Test
    @DisplayName("toResponseDTO: mapea correctamente")
    void toResponseDTO_success() {
        Product p = new Product(1L, null, "Nike", "desc", "Nike", "RUNNING",
                "ATLETISMO", new BigDecimal("100"), 5, null, true);

        ProductResponseDTO result = mapper.toResponseDTO(p);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("toResponseDTO: retorna null si domain es null")
    void toResponseDTO_null() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }
}