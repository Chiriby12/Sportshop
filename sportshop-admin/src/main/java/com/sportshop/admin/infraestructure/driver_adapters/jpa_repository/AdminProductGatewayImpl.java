package com.sportshop.admin.infraestructure.driver_adapters.jpa_repository;

import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.domain.model.gateway.AdminProductGateway;
import com.sportshop.admin.infraestructure.mapper.AdminProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador conducido - implementa el puerto AdminProductGateway.
 */
@Repository
@RequiredArgsConstructor
public class AdminProductGatewayImpl implements AdminProductGateway {

    private final AdminProductJpaRepository jpaRepository;
    private final AdminProductMapper mapper;

    @Override
    public AdminProduct save(AdminProduct product) {
        return mapper.toDomain(jpaRepository.save(mapper.toData(product)));
    }

    @Override
    public Optional<AdminProduct> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AdminProduct> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AdminProduct> findByCategory(String category) {
        return jpaRepository.findByCategory(category).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AdminProduct> findBySport(String sport) {
        return jpaRepository.findBySport(sport).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AdminProduct> findActive() {
        return jpaRepository.findByActiveTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
