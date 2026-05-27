package com.sportshop.catalog.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductData, Long> {
    List<ProductData> findByCategory(String category);
    List<ProductData> findBySport(String sport);
    List<ProductData> findByActiveTrue();
    Optional<ProductData> findByAdminId(Long adminId);
}