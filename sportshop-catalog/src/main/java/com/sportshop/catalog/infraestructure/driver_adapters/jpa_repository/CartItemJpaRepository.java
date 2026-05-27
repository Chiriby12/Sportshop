package com.sportshop.catalog.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemJpaRepository extends JpaRepository<CartItemData, Long> {
    List<CartItemData> findByUserDocument(String userDocument);

    @Transactional
    void deleteAllByUserDocument(String userDocument);
}