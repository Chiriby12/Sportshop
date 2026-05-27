package com.sportshop.auth.infraestructure.driver_adapters.jpa_repository;

import com.sportshop.auth.domain.model.User;
import com.sportshop.auth.domain.model.gateway.UserGateway;
import com.sportshop.auth.infraestructure.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adaptador conducido (Driven Adapter) - implementa el puerto UserGateway.
 * Arquitectura Hexagonal: este adaptador "habla" con la BD y traduce
 * entre el modelo de dominio (User) y la entidad JPA (UserData).
 */
@Repository
@AllArgsConstructor
public class UserDataGatewayImp implements UserGateway {

    private final UserDataJpaRepository userDataJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User saveUser(User user) {
        return userMapper.toUser(userDataJpaRepository.save(userMapper.toUserData(user)));
    }

    @Override
    public User getUserForDocument(String document) {
        return userDataJpaRepository.findById(document)
                .map(userMapper::toUser)
                .orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDataJpaRepository.findByEmail(email)
                .map(userMapper::toUser)
                .orElse(null);
    }

    @Override
    public User updateUser(User user) {
        return userMapper.toUser(userDataJpaRepository.save(userMapper.toUserData(user)));
    }

    @Override
    public void deleteUserForDocument(String document) {
        userDataJpaRepository.deleteById(document);
    }

    @Override
    public List<User> getAllUsers() {
        return userDataJpaRepository.findAll()
                .stream()
                .map(userMapper::toUser)
                .toList();
    }
}
