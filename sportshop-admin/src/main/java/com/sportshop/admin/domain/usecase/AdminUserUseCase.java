package com.sportshop.admin.domain.usecase;

import com.sportshop.admin.domain.model.AdminUser;
import com.sportshop.admin.domain.model.event.AdminEvent;
import com.sportshop.admin.domain.model.gateway.AdminUserGateway;
import com.sportshop.admin.domain.model.gateway.EventPublisherGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class AdminUserUseCase {

    private final AdminUserGateway userGateway;
    private final EventPublisherGateway eventPublisher;

    private static final List<String> ROLES_VALIDOS = List.of("USER", "ADMIN");



    public AdminUser createUser(AdminUser user, String adminDocument) {
        validarUsuario(user);

        if (userGateway.existsByDocument(user.getDocument()))
            throw new RuntimeException("Ya existe un usuario con el documento: " + user.getDocument());

        if (userGateway.existsByEmail(user.getEmail()))
            throw new RuntimeException("Ya existe un usuario con el email: " + user.getEmail());

        if (user.getActive() == null) user.setActive(true);

        AdminUser saved = userGateway.save(user);

        eventPublisher.publish(AdminEvent.of(
                AdminEvent.EventType.USER_CREATED,
                "Nuevo usuario creado",
                "El admin creó el usuario: " + saved.getName() + " (" + saved.getEmail() + ")",
                adminDocument,
                saved
        ));

        return saved;
    }



    public AdminUser getUserByDocument(String document) {
        if (document == null || document.isBlank())
            throw new RuntimeException("El documento no puede estar vacío");

        return userGateway.findByDocument(document)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con documento: " + document));
    }

    public List<AdminUser> getAllUsers() {
        return userGateway.findAll();
    }

    public List<AdminUser> getUsersByRole(String role) {
        if (role == null || role.isBlank())
            throw new RuntimeException("El rol no puede estar vacío");

        String roleUp = role.toUpperCase();
        if (!ROLES_VALIDOS.contains(roleUp))
            throw new RuntimeException("Rol inválido. Use: USER o ADMIN");

        return userGateway.findByRole(roleUp);
    }



    public AdminUser updateUser(String document, AdminUser updatedUser, String adminDocument) {
        if (!userGateway.existsByDocument(document))
            throw new RuntimeException("No existe un usuario con documento: " + document);

        if (updatedUser.getName() == null || updatedUser.getName().isBlank())
            throw new RuntimeException("El nombre no puede estar vacío");

        if (updatedUser.getRole() != null && !ROLES_VALIDOS.contains(updatedUser.getRole().toUpperCase()))
            throw new RuntimeException("Rol inválido. Use: USER o ADMIN");


        AdminUser existing = userGateway.findByDocument(document).get();
        existing.setName(updatedUser.getName());
        existing.setTelephone(updatedUser.getTelephone());
        existing.setAge(updatedUser.getAge());
        if (updatedUser.getActive() != null) existing.setActive(updatedUser.getActive());
        if (updatedUser.getRole() != null) existing.setRole(updatedUser.getRole().toUpperCase());

        AdminUser saved = userGateway.save(existing);

        eventPublisher.publish(AdminEvent.of(
                AdminEvent.EventType.USER_UPDATED,
                "Usuario actualizado",
                "El admin actualizó el usuario: " + saved.getName() + " (" + document + ")",
                adminDocument,
                saved
        ));

        return saved;
    }



    public AdminUser changeUserRole(String document, String newRole, String adminDocument) {
        if (newRole == null || newRole.isBlank())
            throw new RuntimeException("El nuevo rol no puede estar vacío");

        String roleUp = newRole.toUpperCase();
        if (!ROLES_VALIDOS.contains(roleUp))
            throw new RuntimeException("Rol inválido. Use: USER o ADMIN");

        AdminUser user = userGateway.findByDocument(document)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con documento: " + document));

        String oldRole = user.getRole();
        user.setRole(roleUp);
        AdminUser saved = userGateway.save(user);

        eventPublisher.publish(AdminEvent.of(
                AdminEvent.EventType.USER_ROLE_CHANGED,
                "Rol de usuario cambiado",
                "El admin cambió el rol de " + saved.getName() + " de " + oldRole + " a " + roleUp,
                adminDocument,
                saved
        ));

        return saved;
    }



    public void deleteUser(String document, String adminDocument) {
        AdminUser user = userGateway.findByDocument(document)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con documento: " + document));

        userGateway.deleteByDocument(document);

        eventPublisher.publish(AdminEvent.of(
                AdminEvent.EventType.USER_DELETED,
                "Usuario eliminado",
                "El admin eliminó al usuario: " + user.getName() + " (" + document + ")",
                adminDocument,
                user
        ));
    }



    private void validarUsuario(AdminUser user) {
        if (user.getDocument() == null || user.getDocument().isBlank())
            throw new RuntimeException("El documento no puede estar vacío");

        if (user.getName() == null || user.getName().isBlank())
            throw new RuntimeException("El nombre no puede estar vacío");

        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new RuntimeException("El email no puede estar vacío");

        if (!user.getEmail().contains("@"))
            throw new RuntimeException("El email no tiene un formato válido");

        if (user.getRole() != null && !ROLES_VALIDOS.contains(user.getRole().toUpperCase()))
            throw new RuntimeException("Rol inválido. Use: USER o ADMIN");
    }
}
