package com.sportshop.admin.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminProductJpaRepository extends JpaRepository<AdminProductData, Long> {
    List<AdminProductData> findByCategory(String category);
    List<AdminProductData> findBySport(String sport);
    List<AdminProductData> findByActiveTrue();
}
