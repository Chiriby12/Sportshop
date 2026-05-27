package com.sportshop.catalog.domain.model.gateway;

import com.sportshop.catalog.domain.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartGateway {
    CartItem save(CartItem cartItem);
    Optional<CartItem> findById(Long id);
    List<CartItem> findByUserDocument(String userDocument);
    void deleteById(Long id);
    void deleteAllByUserDocument(String userDocument);
}
