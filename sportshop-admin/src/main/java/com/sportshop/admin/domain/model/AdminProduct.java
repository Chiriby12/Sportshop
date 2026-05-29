package com.sportshop.admin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


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
