package com.sportshop.admin.domain.model.gateway;

import com.sportshop.admin.domain.model.AdminUser;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (Output Port) - Arquitectura Hexagonal.
 * El dominio define QUÉ necesita; la infraestructura JPA implementa CÓMO.
 */
public interface AdminUserGateway {
    AdminUser save(AdminUser user);
    Optional<AdminUser> findByDocument(String document);
    Optional<AdminUser> findByEmail(String email);
    List<AdminUser> findAll();
    List<AdminUser> findByRole(String role);
    void deleteByDocument(String document);
    boolean existsByDocument(String document);
    boolean existsByEmail(String email);
}
