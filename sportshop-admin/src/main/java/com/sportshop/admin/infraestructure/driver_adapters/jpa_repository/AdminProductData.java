package com.sportshop.admin.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad JPA - adaptador conducido del hexágono.
 * Tabla en el esquema admin de PostgreSQL.
 */
@Entity
@Table(name = "admin_products")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AdminProductData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String brand;
    private String category;
    private String sport;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean active;
}
