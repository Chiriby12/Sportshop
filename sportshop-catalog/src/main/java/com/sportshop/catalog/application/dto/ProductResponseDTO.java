package com.sportshop.catalog.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ProductResponseDTO {
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
