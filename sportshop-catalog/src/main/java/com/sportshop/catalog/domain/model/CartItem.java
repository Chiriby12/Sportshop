package com.sportshop.catalog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long id;
    private String userDocument;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}
