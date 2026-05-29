package com.sportshop.admin.domain.model.gateway;

import com.sportshop.admin.domain.model.AdminProduct;

import java.util.List;
import java.util.Optional;


public interface AdminProductGateway {
    AdminProduct save(AdminProduct product);
    Optional<AdminProduct> findById(Long id);
    List<AdminProduct> findAll();
    List<AdminProduct> findByCategory(String category);
    List<AdminProduct> findBySport(String sport);
    List<AdminProduct> findActive();
    void deleteById(Long id);
    boolean existsById(Long id);
}
