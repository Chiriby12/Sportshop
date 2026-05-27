package com.sportshop.auth.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDataJpaRepository extends JpaRepository<UserData, String> {
    Optional<UserData> findByEmail(String email);
}
