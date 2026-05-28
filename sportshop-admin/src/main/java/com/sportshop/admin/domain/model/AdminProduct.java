package com.sportshop.admin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad de dominio - Producto gestionado por el Admin.
 * Núcleo del hexágono. Sin Spring, sin JPA.
 *
 * El microservicio admin tiene su propio CRUD de productos
 * Entidad del producto manejada desde el microservicio admin.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminProduct {
    private Long id;
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
