package com.sportshop.auth.infraestructure.driver_adapters.jpa_repository;

import com.sportshop.auth.domain.model.User;
import com.sportshop.auth.infraestructure.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDataGatewayImp - Tests del adaptador JPA")
class UserDataGatewayImpTest {

    @Mock private UserDataJpaRepository jpaRepository;
    @Mock private UserMapper mapper;
    @InjectMocks private UserDataGatewayImp gateway;

    private User user;
    private UserData userData;

    @BeforeEach
    void setUp() {
        user = new User("123", "Ana", "ana@test.com", "hash123", "300", 25, "USER");
        userData = new UserData("123", "Ana", "ana@test.com", "hash123", "300", 25, "USER");
    }

    @Test
    @DisplayName("saveUser: guarda y retorna el dominio mapeado")
    void saveUser_ok() {
        when(mapper.toUserData(user)).thenReturn(userData);
        when(jpaRepository.save(userData)).thenReturn(userData);
        when(mapper.toUser(userData)).thenReturn(user);

        User result = gateway.saveUser(user);

        assertNotNull(result);
        assertEquals("123", result.getDocument());
        verify(jpaRepository).save(userData);
    }

    @Test
    @DisplayName("getUserByDocument: retorna usuario cuando existe")
    void getUserByDocument_existe() {
        when(jpaRepository.findById("123")).thenReturn(Optional.of(userData));
        when(mapper.toUser(userData)).thenReturn(user);

        User result = gateway.getUserByDocument("123");

        assertNotNull(result);
        assertEquals("ana@test.com", result.getEmail());
    }

    @Test
    @DisplayName("getUserByDocument: retorna null cuando no existe")
    void getUserByDocument_noExiste() {
        when(jpaRepository.findById("999")).thenReturn(Optional.empty());

        User result = gateway.getUserByDocument("999");

        assertNull(result);
    }

    @Test
    @DisplayName("getAllUsers: retorna lista mapeada")
    void getAllUsers_ok() {
        when(jpaRepository.findAll()).thenReturn(List.of(userData));
        when(mapper.toUser(userData)).thenReturn(user);

        List<User> result = gateway.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getUserByEmail: retorna usuario cuando existe")
    void getUserByEmail_existe() {
        when(jpaRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(userData));
        when(mapper.toUser(userData)).thenReturn(user);

        User result = gateway.getUserByEmail("ana@test.com");

        assertNotNull(result);
    }

    @Test
    @DisplayName("getUserByEmail: retorna null cuando no existe")
    void getUserByEmail_noExiste() {
        when(jpaRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        User result = gateway.getUserByEmail("noexiste@test.com");

        assertNull(result);
    }

    @Test
    @DisplayName("existsByDocument: retorna true si existe")
    void existsByDocument_true() {
        when(jpaRepository.existsById("123")).thenReturn(true);
        assertTrue(gateway.existsByDocument("123"));
    }

    @Test
    @DisplayName("existsByDocument: retorna false si no existe")
    void existsByDocument_false() {
        when(jpaRepository.existsById("999")).thenReturn(false);
        assertFalse(gateway.existsByDocument("999"));
    }

    @Test
    @DisplayName("existsByEmail: retorna true si el email ya está registrado")
    void existsByEmail_true() {
        when(jpaRepository.existsByEmail("ana@test.com")).thenReturn(true);
        assertTrue(gateway.existsByEmail("ana@test.com"));
    }

    @Test
    @DisplayName("existsByEmail: retorna false si el email no está registrado")
    void existsByEmail_false() {
        when(jpaRepository.existsByEmail("nuevo@test.com")).thenReturn(false);
        assertFalse(gateway.existsByEmail("nuevo@test.com"));
    }

    @Test
    @DisplayName("deleteUser: llama al repositorio con el documento correcto")
    void deleteUser_ok() {
        doNothing().when(jpaRepository).deleteById("123");
        gateway.deleteUser("123");
        verify(jpaRepository).deleteById("123");
    }
}
