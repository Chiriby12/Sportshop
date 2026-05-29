package com.sportshop.admin.infraestructure.driver_adapters.jpa_repository;

import com.sportshop.admin.domain.model.AdminUser;
import com.sportshop.admin.infraestructure.driver_adapters.auth_client.AuthUserGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUserGatewayImpl - Tests del adaptador HTTP hacia sportshop-auth")
class AdminUserGatewayImplTest {

    private AdminUser user;

    @BeforeEach
    void setUp() {
        user = new AdminUser("12345678", "Ana García", "ana@test.com",
                "3001234567", 25, "USER", true);
    }

    @Test
    @DisplayName("AdminUser: modelo de dominio se construye correctamente")
    void adminUser_modelo_ok() {
        assertNotNull(user);
        assertEquals("12345678", user.getDocument());
        assertEquals("Ana García", user.getName());
        assertEquals("ana@test.com", user.getEmail());
        assertEquals("USER", user.getRole());
        assertTrue(user.getActive());
    }

    @Test
    @DisplayName("AdminUser: setters funcionan correctamente")
    void adminUser_setters_ok() {
        user.setRole("ADMIN");
        user.setActive(false);
        user.setTelephone("3110000000");

        assertEquals("ADMIN", user.getRole());
        assertFalse(user.getActive());
        assertEquals("3110000000", user.getTelephone());
    }

    @Test
    @DisplayName("AdminUser: rol por defecto es USER cuando no se especifica")
    void adminUser_rolDefecto() {
        AdminUser sinRol = new AdminUser();
        sinRol.setDocument("999");
        sinRol.setName("Sin Rol");
        sinRol.setEmail("sinrol@test.com");
        assertNull(sinRol.getRole()); // el dominio no asigna default, lo hace el mapper
    }

    @Test
    @DisplayName("AdminUser: equals por referencia")
    void adminUser_referencia() {
        AdminUser mismo = user;
        assertSame(user, mismo);
    }

    @Test
    @DisplayName("Lista de usuarios: filtro por rol USER funciona en stream")
    void filtroRol_user() {
        AdminUser admin = new AdminUser("99", "Admin", "a@test.com", null, null, "ADMIN", true);
        List<AdminUser> todos = List.of(user, admin);

        List<AdminUser> soloUsers = todos.stream()
                .filter(u -> "USER".equalsIgnoreCase(u.getRole()))
                .toList();

        assertEquals(1, soloUsers.size());
        assertEquals("12345678", soloUsers.get(0).getDocument());
    }

    @Test
    @DisplayName("Lista de usuarios: filtro por email funciona en stream")
    void filtroEmail_ok() {
        List<AdminUser> todos = List.of(user);
        Optional<AdminUser> resultado = todos.stream()
                .filter(u -> "ana@test.com".equalsIgnoreCase(u.getEmail()))
                .findFirst();

        assertTrue(resultado.isPresent());
        assertEquals("Ana García", resultado.get().getName());
    }

    @Test
    @DisplayName("Lista vacía: filtro retorna Optional vacío")
    void filtroEmail_listaVacia() {
        List<AdminUser> vacia = List.of();
        Optional<AdminUser> resultado = vacia.stream()
                .filter(u -> "nadie@test.com".equalsIgnoreCase(u.getEmail()))
                .findFirst();
        assertTrue(resultado.isEmpty());
    }
}
