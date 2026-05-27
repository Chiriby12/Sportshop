package com.sportshop.auth.domain.model.gateway;

import com.sportshop.auth.domain.model.User;
import java.util.List;

public interface UserGateway {
    User saveUser(User user);
    User getUserForDocument(String document);
    User getUserByEmail(String email);
    User updateUser(User user);
    void deleteUserForDocument(String document);
    List<User> getAllUsers();
}
