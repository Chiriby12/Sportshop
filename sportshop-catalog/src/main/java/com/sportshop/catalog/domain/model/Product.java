package com.sportshop.catalog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Modelo de dominio puro - sin anotaciones de Spring ni JPA.
 * Arquitectura Hexagonal: núcleo del hexágono.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private Long adminId;       // ID del producto en sportshop-admin (para sincronización)
    private String name;
    private String description;
    private String brand;
    private String category;
    private String sport;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Boolean active;
}