package com.sportshop.admin.infraestructure.entry_points;

import com.sportshop.admin.application.dto.*;
import com.sportshop.admin.domain.model.AdminUser;
import com.sportshop.admin.domain.usecase.AdminUserUseCase;
import com.sportshop.admin.infraestructure.mapper.AdminUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador conductor (Driving Adapter) - CRUD de usuarios para el Admin.
 * Delega la persistencia a sportshop-auth via AuthUserGatewayImpl.
 */
@RestController
@RequestMapping("/api/sportshop/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin - Usuarios", description = "CRUD completo de usuarios. Solo ADMIN. Persiste en sportshop-auth.")
public class AdminUserController {

    private final AdminUserUseCase userUseCase;
    private final AdminUserMapper userMapper;

    @PostMapping
    @Operation(summary = "Crear usuario",
               description = "Crea el usuario en sportshop-auth. Contraseña temporal: SportShop2025!")
    public ResponseEntity<ApiResponse<AdminUser>> createUser(
            @Valid @RequestBody AdminUserRequestDTO dto,
            Authentication auth) {
        AdminUser saved = userUseCase.createUser(userMapper.toDomain(dto), getDocument(auth));
        return new ResponseEntity<>(ApiResponse.created("Usuario creado exitosamente", saved), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Lee desde sportshop-auth")
    public ResponseEntity<ApiResponse<List<AdminUser>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios encontrados", userUseCase.getAllUsers()));
    }

    @GetMapping("/{document}")
    @Operation(summary = "Obtener usuario por documento")
    public ResponseEntity<ApiResponse<AdminUser>> getUserByDocument(@PathVariable String document) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado", userUseCase.getUserByDocument(document)));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Usuarios por rol", description = "Filtra por USER o ADMIN")
    public ResponseEntity<ApiResponse<List<AdminUser>>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios con rol " + role, userUseCase.getUsersByRole(role)));
    }

    @PutMapping("/{document}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<ApiResponse<AdminUser>> updateUser(
            @PathVariable String document,
            @Valid @RequestBody AdminUserUpdateDTO dto,
            Authentication auth) {
        AdminUser updated = userUseCase.updateUser(
                document, userMapper.toDomainFromUpdate(document, dto), getDocument(auth));
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado exitosamente", updated));
    }

    @PatchMapping("/{document}/role")
    @Operation(summary = "Cambiar rol de usuario")
    public ResponseEntity<ApiResponse<AdminUser>> changeRole(
            @PathVariable String document,
            @RequestParam String role,
            Authentication auth) {
        AdminUser updated = userUseCase.changeUserRole(document, role, getDocument(auth));
        return ResponseEntity.ok(ApiResponse.ok("Rol actualizado a " + role, updated));
    }

    @DeleteMapping("/{document}")
    @Operation(summary = "Eliminar usuario", description = "Elimina de sportshop-auth")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String document,
            Authentication auth) {
        userUseCase.deleteUser(document, getDocument(auth));
        return ResponseEntity.ok(ApiResponse.ok("Usuario " + document + " eliminado correctamente", null));
    }

    private String getDocument(Authentication auth) {
        if (auth != null && auth.getDetails() instanceof String doc && !doc.isBlank()) return doc;
        if (auth != null) return auth.getName();
        return "unknown-admin";
    }
}
