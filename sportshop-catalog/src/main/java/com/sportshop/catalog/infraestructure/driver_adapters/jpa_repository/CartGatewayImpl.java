package com.sportshop.catalog.infraestructure.driver_adapters.jpa_repository;

import com.sportshop.catalog.domain.model.CartItem;
import com.sportshop.catalog.domain.model.gateway.CartGateway;
import com.sportshop.catalog.infraestructure.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartGatewayImpl implements CartGateway {

    private final CartItemJpaRepository jpaRepository;
    private final CartMapper mapper;

    @Override
    public CartItem save(CartItem cartItem) {
        return mapper.toDomain(jpaRepository.save(mapper.toData(cartItem)));
    }

    @Override
    public Optional<CartItem> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CartItem> findByUserDocument(String userDocument) {
        return jpaRepository.findByUserDocument(userDocument).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByUserDocument(String userDocument) {
        jpaRepository.deleteAllByUserDocument(userDocument);
    }
}
