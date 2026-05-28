package com.sportshop.catalog.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotBlank(message = "La marca es obligatoria")
    private String brand;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @NotBlank(message = "El deporte es obligatorio")
    private String sport;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private String imageUrl;
    private Boolean active;
}
