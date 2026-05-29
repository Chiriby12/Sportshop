package com.sportshop.auth.domain.usecase;

import com.sportshop.auth.domain.model.User;
import com.sportshop.auth.domain.model.gateway.EncrypterGateway;
import com.sportshop.auth.domain.model.gateway.JwtGateway;
import com.sportshop.auth.domain.model.gateway.UserGateway;
import com.sportshop.auth.infraestructure.driver_adapters.jpa_repository.UserData;
import com.sportshop.auth.infraestructure.driver_adapters.jpa_repository.UserDataGatewayImp;
import com.sportshop.auth.infraestructure.driver_adapters.jpa_repository.UserDataJpaRepository;
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
class UserUseCaseTest {

    @Mock private UserGateway userGateway;
    @Mock private EncrypterGateway encrypterGateway;
    @Mock private JwtGateway jwtGateway;
    @InjectMocks private UserUseCase userUseCase;

    @Mock private UserDataJpaRepository userDataJpaRepository;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserDataGatewayImp userDataGatewayImp;

    private User usuario;
    private UserData userData;

    @BeforeEach
    void setUp() {
        usuario = new User("1234567890", "Juan Pérez", "juan@mail.com",
                "ClaveSegura1!", "3001234567", 25, "USER");
        userData = new UserData("1234567890", "Juan Pérez", "juan@mail.com",
                "ClaveSegura1!", "3001234567", 25, "USER");
    }



    @Test
    @DisplayName("saveUser: guarda correctamente un usuario nuevo")
    void saveUser_exitoso() {
        when(userGateway.getUserForDocument("1234567890")).thenReturn(null);
        when(userGateway.getUserByEmail("juan@mail.com")).thenReturn(null);
        when(encrypterGateway.encrypt("ClaveSegura1!")).thenReturn("$hash$");
        when(userGateway.saveUser(any())).thenReturn(usuario);

        User resultado = userUseCase.saveUser(usuario);

        assertNotNull(resultado);
        assertEquals("juan@mail.com", resultado.getEmail());
        verify(encrypterGateway).encrypt("ClaveSegura1!");
        verify(userGateway).saveUser(any());
    }

    @Test
    @DisplayName("saveUser: asigna rol USER por defecto cuando el rol es null")
    void saveUser_rolNullAsignaUser() {
        usuario.setRole(null);
        when(userGateway.getUserForDocument(any())).thenReturn(null);
        when(userGateway.getUserByEmail(any())).thenReturn(null);
        when(encrypterGateway.encrypt(any())).thenReturn("$hash$");
        when(userGateway.saveUser(any())).thenReturn(usuario);

        userUseCase.saveUser(usuario);

        assertEquals("USER", usuario.getRole());
    }

    @Test
    @DisplayName("saveUser: asigna rol USER por defecto cuando el rol es vacío")
    void saveUser_rolVacioAsignaUser() {
        usuario.setRole("  ");
        when(userGateway.getUserForDocument(any())).thenReturn(null);
        when(userGateway.getUserByEmail(any())).thenReturn(null);
        when(encrypterGateway.encrypt(any())).thenReturn("$hash$");
        when(userGateway.saveUser(any())).thenReturn(usuario);

        userUseCase.saveUser(usuario);

        assertEquals("USER", usuario.getRole());
    }

    @Test
    @DisplayName("saveUser: lanza error si el email está vacío")
    void saveUser_emailVacio() {
        usuario.setEmail("");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userUseCase.saveUser(usuario));
        assertEquals("El email no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("saveUser: lanza error si el email es null")
    void saveUser_emailNull() {
        usuario.setEmail(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userUseCase.saveUser(usuario));
        assertEquals("El email no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("saveUser: lanza error si la contraseña está vacía")
    void saveUser_passwordVacio() {
        usuario.setPassword("");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userUseCase.saveUser(usuario));
        assertEquals("La contraseña no puede estar vacía", ex.getMessage());
    }

    @Test
    @DisplayName("saveUser: lanza error si el documento ya existe")
    void saveUser_documentoDuplicado() {
        when(userGateway.getUserForDocument("1234567890")).thenReturn(usuario);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userUseCase.saveUser(usuario));
        assertTrue(ex.getMessage().contains("Ya existe un usuario con el documento"));
    }

    @Test
    @DisplayName("saveUser: lanza error si el email ya existe")
    void saveUser_emailDuplicado() {
        when(userGateway.getUserForDocument("1234567890")).thenReturn(null);
        when(userGateway.getUserByEmail("juan@mail.com")).thenReturn(usuario);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userUseCase.saveUser(usuario));
        assertTrue(ex.getMessage().contains("Ya existe un usuario con el email"));
    }



    @Test
    @DisplayName("getUserForDocument: retorna usuario cuando existe")
    void getUserForDocument_exitoso() {
        when(userGateway.getUserForDocument("1234567890")).thenReturn(usuario);
        User result = userUseCase.getUserForDocument("1234567890");
        assertNotNull(result);
        assertEquals("1234567890", result.getDocument());
    }

    @Test
    @DisplayName("getUserForDocument: lanza error cuando no existe")
    void getUserForDocument_noExiste() {
        when(userGateway.getUserForDocument("9999")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userUseCase.getUserForDocument("9999"));
        assertTrue(ex.getMessage().contains("No existe un usuario con el documento"));
    }



    @Test
    @DisplayName("getAllUsers: retorna lista de usuarios")
    void getAllUsers_exitoso() {
        when(userGateway.getAllUsers()).thenReturn(List.of(usuario));
        List<User> result = userUseCase.getAllUsers();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }



    @Test
    @DisplayName("deleteUserForDocument: elimina correctamente")
    void deleteUserForDocument_exitoso() {
        when(userGateway.getUserForDocument("1234567890")).thenReturn(usuario);
        doNothing().when(userGateway).deleteUserForDocument("1234567890");

        assertDoesNotThrow(() -> userUseCase.deleteUserForDocument("1234567890"));
        verify(userGateway).deleteUserForDocument("1234567890");
    }

    @Test
    @DisplayName("deleteUserForDocument: lanza error si no existe")
    void deleteUserForDocument_noExiste() {
        when(userGateway.getUserForDocument("9999")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userUseCase.deleteUserForDocument("9999"));
        assertTrue(ex.getMessage().contains("No existe un usuario con el documento"));
    }



    @Test
    @DisplayName("updateUser: actualiza correctamente con contraseña nueva")
    void updateUser_exitosoConPassword() {
        when(userGateway.getUserForDocument("1234567890")).thenReturn(usuario);
        when(encrypterGateway.encrypt("NuevaClave!")).thenReturn("$newhash$");
        when(userGateway.updateUser(any())).thenReturn(usuario);

        User update = new User("1234567890", "Juan", "juan@mail.com", "NuevaClave!", "300", 25, "USER");
        User result = userUseCase.updateUser("1234567890", update);

        assertNotNull(result);
        verify(encrypterGateway).encrypt("NuevaClave!");
    }

    @Test
    @DisplayName("updateUser: conserva contraseña anterior si no se envía")
    void updateUser_sinPassword() {
        when(userGateway.getUserForDocument("1234567890")).thenReturn(usuario);
        when(userGateway.updateUser(any())).thenReturn(usuario);

        User update = new User("1234567890", "Juan", "juan@mail.com", null, "300", 25, "USER");
        userUseCase.updateUser("1234567890", update);

        verify(encrypterGateway, never()).encrypt(any());
        assertEquals("ClaveSegura1!", update.getPassword());
    }

    @Test
    @DisplayName("updateUser: conserva contraseña anterior si está vacía")
    void updateUser_passwordVacia() {
        when(userGateway.getUserForDocument("1234567890")).thenReturn(usuario);
        when(userGateway.updateUser(any())).thenReturn(usuario);

        User update = new User("1234567890", "Juan", "juan@mail.com", "  ", "300", 25, "USER");
        userUseCase.updateUser("1234567890", update);

        verify(encrypterGateway, never()).encrypt(any());
    }

    @Test
    @DisplayName("updateUser: lanza error si documentos no coinciden")
    void updateUser_documentoNoCoincide() {
        User update = new User("9999", "Juan", "juan@mail.com", "clave", "300", 25, "USER");
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userUseCase.updateUser("1234567890", update));
        assertTrue(ex.getMessage().contains("no coincide"));
    }

    @Test
    @DisplayName("updateUser: lanza error si el usuario no existe")
    void updateUser_usuarioNoExiste() {
        when(userGateway.getUserForDocument("1234567890")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userUseCase.updateUser("1234567890", usuario));
        assertTrue(ex.getMessage().contains("No existe un usuario con el documento"));
    }



    @Test
    @DisplayName("loginUser: retorna token con credenciales correctas")
    void loginUser_exitoso() {
        when(userGateway.getUserByEmail("juan@mail.com")).thenReturn(usuario);
        when(encrypterGateway.matches("ClaveSegura1!", "ClaveSegura1!")).thenReturn(true);
        when(jwtGateway.generateToken(usuario)).thenReturn("fake.jwt.token");

        String token = userUseCase.loginUser("juan@mail.com", "ClaveSegura1!");
        assertEquals("fake.jwt.token", token);
    }

    @Test
    @DisplayName("loginUser: lanza error si email vacío")
    void loginUser_emailVacio() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userUseCase.loginUser("", "clave"));
        assertEquals("El email no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("loginUser: lanza error si password vacío")
    void loginUser_passwordVacio() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userUseCase.loginUser("juan@mail.com", ""));
        assertEquals("La contraseña no puede estar vacía", ex.getMessage());
    }

    @Test
    @DisplayName("loginUser: lanza error si usuario no existe")
    void loginUser_usuarioNoExiste() {
        when(userGateway.getUserByEmail("noexiste@mail.com")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userUseCase.loginUser("noexiste@mail.com", "clave"));
        assertTrue(ex.getMessage().contains("No existe un usuario con el email"));
    }

    @Test
    @DisplayName("loginUser: lanza error si contraseña incorrecta")
    void loginUser_passwordIncorrecta() {
        when(userGateway.getUserByEmail("juan@mail.com")).thenReturn(usuario);
        when(encrypterGateway.matches("MalaClave", "ClaveSegura1!")).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userUseCase.loginUser("juan@mail.com", "MalaClave"));
        assertEquals("Contraseña incorrecta", ex.getMessage());
    }



    @Test
    @DisplayName("UserDataGatewayImp: saveUser persiste y retorna usuario")
    void gateway_saveUser() {
        when(userMapper.toUserData(usuario)).thenReturn(userData);
        when(userDataJpaRepository.save(userData)).thenReturn(userData);
        when(userMapper.toUser(userData)).thenReturn(usuario);

        User result = userDataGatewayImp.saveUser(usuario);
        assertNotNull(result);
    }

    @Test
    @DisplayName("UserDataGatewayImp: getUserForDocument retorna usuario cuando existe")
    void gateway_getUserForDocument_existe() {
        when(userDataJpaRepository.findById("1234567890")).thenReturn(Optional.of(userData));
        when(userMapper.toUser(userData)).thenReturn(usuario);

        User result = userDataGatewayImp.getUserForDocument("1234567890");
        assertNotNull(result);
    }

    @Test
    @DisplayName("UserDataGatewayImp: getUserForDocument retorna null cuando no existe")
    void gateway_getUserForDocument_noExiste() {
        when(userDataJpaRepository.findById("9999")).thenReturn(Optional.empty());
        assertNull(userDataGatewayImp.getUserForDocument("9999"));
    }

    @Test
    @DisplayName("UserDataGatewayImp: getUserByEmail retorna usuario cuando existe")
    void gateway_getUserByEmail_existe() {
        when(userDataJpaRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(userData));
        when(userMapper.toUser(userData)).thenReturn(usuario);

        User result = userDataGatewayImp.getUserByEmail("juan@mail.com");
        assertNotNull(result);
    }

    @Test
    @DisplayName("UserDataGatewayImp: getUserByEmail retorna null cuando no existe")
    void gateway_getUserByEmail_noExiste() {
        when(userDataJpaRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());
        assertNull(userDataGatewayImp.getUserByEmail("noexiste@mail.com"));
    }

    @Test
    @DisplayName("UserDataGatewayImp: deleteUserForDocument llama al repositorio")
    void gateway_deleteUser() {
        doNothing().when(userDataJpaRepository).deleteById("1234567890");
        assertDoesNotThrow(() -> userDataGatewayImp.deleteUserForDocument("1234567890"));
        verify(userDataJpaRepository).deleteById("1234567890");
    }

    @Test
    @DisplayName("UserDataGatewayImp: getAllUsers retorna lista")
    void gateway_getAllUsers() {
        when(userDataJpaRepository.findAll()).thenReturn(List.of(userData));
        when(userMapper.toUser(userData)).thenReturn(usuario);

        List<User> result = userDataGatewayImp.getAllUsers();
        assertEquals(1, result.size());
    }
}