package com.sportshop.auth.domain.usecase;

import com.sportshop.auth.domain.model.User;
import com.sportshop.auth.domain.model.gateway.EncrypterGateway;
import com.sportshop.auth.domain.model.gateway.JwtGateway;
import com.sportshop.auth.domain.model.gateway.UserGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Caso de uso principal del dominio de autenticación.
 * Arquitectura Hexagonal: este clase es el NÚCLEO del hexágono.
 * No tiene dependencias de Spring ni de infraestructura.
 * Solo interactúa con puertos (interfaces/gateways).
 */
@RequiredArgsConstructor
public class UserUseCase {

    private final UserGateway userGateway;
    private final EncrypterGateway encrypterGateway;
    private final JwtGateway jwtGateway;

    public User saveUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty())
            throw new RuntimeException("El email no puede estar vacío");
        if (user.getPassword() == null || user.getPassword().trim().isEmpty())
            throw new RuntimeException("La contraseña no puede estar vacía");

        if (userGateway.getUserForDocument(user.getDocument()) != null)
            throw new RuntimeException("Ya existe un usuario con el documento: " + user.getDocument());

        if (userGateway.getUserByEmail(user.getEmail()) != null)
            throw new RuntimeException("Ya existe un usuario con el email: " + user.getEmail());

        // Por defecto, si no se especifica rol, es USER
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }

        user.setPassword(encrypterGateway.encrypt(user.getPassword()));
        return userGateway.saveUser(user);
    }

    public User getUserForDocument(String document) {
        User user = userGateway.getUserForDocument(document);
        if (user == null)
            throw new RuntimeException("No existe un usuario con el documento: " + document);
        return user;
    }

    public List<User> getAllUsers() {
        return userGateway.getAllUsers();
    }

    public void deleteUserForDocument(String document) {
        if (userGateway.getUserForDocument(document) == null)
            throw new RuntimeException("No existe un usuario con el documento: " + document);
        userGateway.deleteUserForDocument(document);
    }

    public User updateUser(String documentUrl, User user) {
        if (!documentUrl.equals(user.getDocument()))
            throw new RuntimeException("El documento de la URL no coincide con el del body");

        User existing = userGateway.getUserForDocument(user.getDocument());
        if (existing == null)
            throw new RuntimeException("No existe un usuario con el documento: " + user.getDocument());

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(encrypterGateway.encrypt(user.getPassword()));
        }

        return userGateway.updateUser(user);
    }

    public String loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty())
            throw new RuntimeException("El email no puede estar vacío");
        if (password == null || password.trim().isEmpty())
            throw new RuntimeException("La contraseña no puede estar vacía");

        User user = userGateway.getUserByEmail(email);
        if (user == null)
            throw new RuntimeException("No existe un usuario con el email: " + email);

        if (!encrypterGateway.matches(password, user.getPassword()))
            throw new RuntimeException("Contraseña incorrecta");

        return jwtGateway.generateToken(user);
    }
}
