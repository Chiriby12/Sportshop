package com.sportshop.auth.infraestructure.entry_points;

import com.sportshop.auth.application.dto.*;
import com.sportshop.auth.domain.model.User;
import com.sportshop.auth.domain.model.gateway.UserGateway;
import com.sportshop.auth.domain.usecase.UserUseCase;
import com.sportshop.auth.infraestructure.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador conductor (Driving Adapter) - expone los casos de uso vía HTTP REST.
 * Arquitectura Hexagonal: este controlador es el punto de entrada al hexágono.
 * Solo traduce HTTP -> dominio y dominio -> HTTP. Nunca contiene lógica de negocio.
 */
@RestController
@RequestMapping("/api/sportshop/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de registro, login y gestión de usuarios")
public class UserController {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final UserGateway userGateway;

    @PostMapping("/save")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario. Role: USER o ADMIN")
    public ResponseEntity<ApiResponse<UserResponseDTO>> saveUser(
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        User user = userUseCase.saveUser(userMapper.toUserFromDTO(userRequestDTO));
        return new ResponseEntity<>(
                ApiResponse.created("Usuario registrado exitosamente", userMapper.toUserResponseDTO(user)),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y retorna un token JWT")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginUser(
            @Valid @RequestBody LoginRequestDTO request) {
        String token = userUseCase.loginUser(request.getEmail(), request.getPassword());
        User user = userGateway.getUserByEmail(request.getEmail());
        LoginResponseDTO loginData = new LoginResponseDTO(token, userMapper.toUserResponseDTO(user));
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", loginData));
    }

    @GetMapping("/get/{document}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener usuario por documento",
            description = "ADMIN: puede ver cualquier usuario. USER: solo puede ver su propio perfil")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserForDocument(
            @PathVariable String document) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (esUsuarioNormal(auth)) {
            User currentUser = userGateway.getUserByEmail(auth.getName());
            if (!currentUser.getDocument().equals(document))
                throw new RuntimeException("No tienes permiso para ver el perfil de otro usuario");
        }
        User user = userUseCase.getUserForDocument(document);
        return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado", userMapper.toUserResponseDTO(user)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar todos los usuarios", description = "Solo ADMIN")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userUseCase.getAllUsers()
                .stream()
                .map(userMapper::toUserResponseDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Usuarios encontrados", users));
    }

    @PutMapping("/update/{document}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar usuario",
            description = "ADMIN: puede actualizar cualquier usuario. USER: solo el suyo")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable String document,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (esUsuarioNormal(auth)) {
            User currentUser = userGateway.getUserByEmail(auth.getName());
            if (!currentUser.getDocument().equals(document))
                throw new RuntimeException("No tienes permiso para modificar el perfil de otro usuario");
        }
        User user = userUseCase.updateUser(document, userMapper.toUserFromUpdateDTO(userUpdateDTO));
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado exitosamente", userMapper.toUserResponseDTO(user)));
    }

    @DeleteMapping("/delete/{document}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar usuario", description = "Solo ADMIN")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String document) {
        userUseCase.deleteUserForDocument(document);
        return ResponseEntity.ok(ApiResponse.ok("Usuario " + document + " eliminado correctamente", null));
    }

    private boolean esUsuarioNormal(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }
}
