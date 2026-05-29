package com.sportshop.catalog.domain.model.gateway;

import com.sportshop.catalog.domain.model.Product;

import java.util.List;
import java.util.Optional;


public interface ProductGateway {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    List<Product> findBySport(String sport);
    List<Product> findActive();
    void deleteById(Long id);
    boolean existsById(Long id);
    Optional<Product> findByAdminId(Long adminId);
    void deleteByAdminId(Long adminId);
}