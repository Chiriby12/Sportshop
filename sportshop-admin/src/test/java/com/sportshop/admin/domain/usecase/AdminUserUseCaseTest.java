package com.sportshop.admin.domain.usecase;

import com.sportshop.admin.domain.model.AdminUser;
import com.sportshop.admin.domain.model.event.AdminEvent;
import com.sportshop.admin.domain.model.gateway.AdminUserGateway;
import com.sportshop.admin.domain.model.gateway.EventPublisherGateway;
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
class AdminUserUseCaseTest {

    @Mock private AdminUserGateway userGateway;
    @Mock private EventPublisherGateway eventPublisher;
    @InjectMocks private AdminUserUseCase useCase;

    private AdminUser user;

    @BeforeEach
    void setUp() {
        user = new AdminUser("12345678", "Ana García", "ana@test.com", "3001234567", 25, "USER", true);
    }

    // ══════════ createUser ══════════

    @Test
    @DisplayName("createUser: crea usuario correctamente")
    void createUser_exitoso() {
        when(userGateway.existsByDocument("12345678")).thenReturn(false);
        when(userGateway.existsByEmail("ana@test.com")).thenReturn(false);
        when(userGateway.save(any())).thenReturn(user);

        AdminUser result = useCase.createUser(user, "admin-doc");

        assertNotNull(result);
        assertEquals("12345678", result.getDocument());
        verify(eventPublisher).publish(any(AdminEvent.class));
    }

    @Test
    @DisplayName("createUser: asigna active=true si viene null")
    void createUser_activePorDefecto() {
        user.setActive(null);
        when(userGateway.existsByDocument(any())).thenReturn(false);
        when(userGateway.existsByEmail(any())).thenReturn(false);
        when(userGateway.save(any())).thenReturn(user);

        useCase.createUser(user, "admin");

        assertTrue(user.getActive());
    }

    @Test
    @DisplayName("createUser: error si documento vacío")
    void createUser_documentoVacio() {
        user.setDocument("  ");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.createUser(user, "admin"));
        assertEquals("El documento no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("createUser: error si documento null")
    void createUser_documentoNull() {
        user.setDocument(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.createUser(user, "admin"));
        assertEquals("El documento no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("createUser: error si nombre vacío")
    void createUser_nombreVacio() {
        user.setName("");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.createUser(user, "admin"));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("createUser: error si email vacío")
    void createUser_emailVacio() {
        user.setEmail("  ");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.createUser(user, "admin"));
        assertEquals("El email no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("createUser: error si email sin @")
    void createUser_emailInvalido() {
        user.setEmail("sin-arroba.com");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.createUser(user, "admin"));
        assertEquals("El email no tiene un formato válido", ex.getMessage());
    }

    @Test
    @DisplayName("createUser: error si rol inválido")
    void createUser_rolInvalido() {
        user.setRole("SUPERADMIN");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.createUser(user, "admin"));
        assertTrue(ex.getMessage().contains("Rol inválido"));
    }

    @Test
    @DisplayName("createUser: error si documento ya existe")
    void createUser_documentoDuplicado() {
        when(userGateway.existsByDocument("12345678")).thenReturn(true);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.createUser(user, "admin"));
        assertTrue(ex.getMessage().contains("Ya existe un usuario con el documento"));
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("createUser: error si email ya existe")
    void createUser_emailDuplicado() {
        when(userGateway.existsByDocument("12345678")).thenReturn(false);
        when(userGateway.existsByEmail("ana@test.com")).thenReturn(true);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.createUser(user, "admin"));
        assertTrue(ex.getMessage().contains("Ya existe un usuario con el email"));
    }

    // ══════════ getUserByDocument ══════════

    @Test
    @DisplayName("getUserByDocument: retorna usuario existente")
    void getUserByDocument_exitoso() {
        when(userGateway.findByDocument("12345678")).thenReturn(Optional.of(user));
        AdminUser result = useCase.getUserByDocument("12345678");
        assertEquals("12345678", result.getDocument());
    }

    @Test
    @DisplayName("getUserByDocument: error si no existe")
    void getUserByDocument_noExiste() {
        when(userGateway.findByDocument("99999999")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.getUserByDocument("99999999"));
        assertTrue(ex.getMessage().contains("No existe un usuario con documento"));
    }

    @Test
    @DisplayName("getUserByDocument: error si documento vacío")
    void getUserByDocument_vacio() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.getUserByDocument("  "));
        assertEquals("El documento no puede estar vacío", ex.getMessage());
    }

    // ══════════ getAllUsers ══════════

    @Test
    @DisplayName("getAllUsers: retorna lista de usuarios")
    void getAllUsers_exitoso() {
        when(userGateway.findAll()).thenReturn(List.of(user));
        List<AdminUser> result = useCase.getAllUsers();
        assertEquals(1, result.size());
    }

    // ══════════ getUsersByRole ══════════

    @Test
    @DisplayName("getUsersByRole: filtra por rol USER")
    void getUsersByRole_exitoso() {
        when(userGateway.findByRole("USER")).thenReturn(List.of(user));
        List<AdminUser> result = useCase.getUsersByRole("USER");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getUsersByRole: acepta minúsculas")
    void getUsersByRole_minusculas() {
        when(userGateway.findByRole("ADMIN")).thenReturn(List.of());
        assertDoesNotThrow(() -> useCase.getUsersByRole("admin"));
        verify(userGateway).findByRole("ADMIN");
    }

    @Test
    @DisplayName("getUsersByRole: error si rol vacío")
    void getUsersByRole_vacio() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.getUsersByRole("  "));
        assertEquals("El rol no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("getUsersByRole: error si rol inválido")
    void getUsersByRole_invalido() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.getUsersByRole("SUPERUSER"));
        assertTrue(ex.getMessage().contains("Rol inválido"));
    }

    // ══════════ updateUser ══════════

    @Test
    @DisplayName("updateUser: actualiza correctamente")
    void updateUser_exitoso() {
        AdminUser updated = new AdminUser("12345678", "Ana Nueva", null, "3009999999", 26, "ADMIN", true);
        when(userGateway.existsByDocument("12345678")).thenReturn(true);
        when(userGateway.findByDocument("12345678")).thenReturn(Optional.of(user));
        when(userGateway.save(any())).thenReturn(user);

        AdminUser result = useCase.updateUser("12345678", updated, "admin-doc");

        assertNotNull(result);
        verify(eventPublisher).publish(any(AdminEvent.class));
    }

    @Test
    @DisplayName("updateUser: error si usuario no existe")
    void updateUser_noExiste() {
        when(userGateway.existsByDocument("99999")).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.updateUser("99999", user, "admin"));
        assertTrue(ex.getMessage().contains("No existe un usuario con documento"));
    }

    @Test
    @DisplayName("updateUser: error si nombre vacío")
    void updateUser_nombreVacio() {
        when(userGateway.existsByDocument("12345678")).thenReturn(true);
        AdminUser badUpdate = new AdminUser("12345678", "  ", null, null, null, "USER", true);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.updateUser("12345678", badUpdate, "admin"));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("updateUser: error si rol inválido en update")
    void updateUser_rolInvalido() {
        when(userGateway.existsByDocument("12345678")).thenReturn(true);
        AdminUser badUpdate = new AdminUser("12345678", "Ana", null, null, null, "BADROL", true);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.updateUser("12345678", badUpdate, "admin"));
        assertTrue(ex.getMessage().contains("Rol inválido"));
    }

    // ══════════ changeUserRole ══════════

    @Test
    @DisplayName("changeUserRole: cambia rol correctamente")
    void changeUserRole_exitoso() {
        when(userGateway.findByDocument("12345678")).thenReturn(Optional.of(user));
        when(userGateway.save(any())).thenReturn(user);

        AdminUser result = useCase.changeUserRole("12345678", "ADMIN", "super-admin");

        assertNotNull(result);
        verify(eventPublisher).publish(any(AdminEvent.class));
    }

    @Test
    @DisplayName("changeUserRole: acepta minúsculas")
    void changeUserRole_minusculas() {
        when(userGateway.findByDocument("12345678")).thenReturn(Optional.of(user));
        when(userGateway.save(any())).thenReturn(user);
        assertDoesNotThrow(() -> useCase.changeUserRole("12345678", "admin", "super-admin"));
    }

    @Test
    @DisplayName("changeUserRole: error si rol vacío")
    void changeUserRole_rolVacio() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.changeUserRole("12345678", "  ", "admin"));
        assertEquals("El nuevo rol no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("changeUserRole: error si rol inválido")
    void changeUserRole_rolInvalido() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.changeUserRole("12345678", "GOD", "admin"));
        assertTrue(ex.getMessage().contains("Rol inválido"));
    }

    @Test
    @DisplayName("changeUserRole: error si usuario no existe")
    void changeUserRole_noExiste() {
        when(userGateway.findByDocument("99999")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.changeUserRole("99999", "ADMIN", "admin"));
        assertTrue(ex.getMessage().contains("No existe un usuario con documento"));
    }

    // ══════════ deleteUser ══════════

    @Test
    @DisplayName("deleteUser: elimina correctamente")
    void deleteUser_exitoso() {
        when(userGateway.findByDocument("12345678")).thenReturn(Optional.of(user));
        doNothing().when(userGateway).deleteByDocument("12345678");

        assertDoesNotThrow(() -> useCase.deleteUser("12345678", "admin-doc"));
        verify(userGateway).deleteByDocument("12345678");
        verify(eventPublisher).publish(any(AdminEvent.class));
    }

    @Test
    @DisplayName("deleteUser: error si no existe")
    void deleteUser_noExiste() {
        when(userGateway.findByDocument("99999")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.deleteUser("99999", "admin"));
        assertTrue(ex.getMessage().contains("No existe un usuario con documento"));
        verify(userGateway, never()).deleteByDocument(any());
    }
}
