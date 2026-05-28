package com.sportshop.catalog.infraestructure.driver_adapters.jpa_repository;

import com.sportshop.catalog.domain.model.Product;
import com.sportshop.catalog.domain.model.gateway.ProductGateway;
import com.sportshop.catalog.infraestructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductGatewayImpl implements ProductGateway {

    private final ProductJpaRepository jpaRepository;
    private final ProductMapper mapper;

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaRepository.save(mapper.toData(product)));
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Product> findByCategory(String category) {
        return jpaRepository.findByCategory(category).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Product> findBySport(String sport) {
        return jpaRepository.findBySport(sport).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Product> findActive() {
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

    @Override
    public Optional<Product> findByAdminId(Long adminId) {
        return jpaRepository.findByAdminId(adminId).map(mapper::toDomain);
    }

    @Override
    public void deleteByAdminId(Long adminId) {
        jpaRepository.findByAdminId(adminId).ifPresent(p -> jpaRepository.deleteById(p.getId()));
    }
}