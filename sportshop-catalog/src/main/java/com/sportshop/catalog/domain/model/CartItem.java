package com.sportshop.catalog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representa un ítem en el carrito de un usuario.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long id;
    private String userDocument;    // documento del usuario dueño del carrito
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}
