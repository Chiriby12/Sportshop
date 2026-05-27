package com.sportshop.auth.domain.model.gateway;

import com.sportshop.auth.domain.model.User;

public interface JwtGateway {
    String generateToken(User user);
    String extractEmail(String token);
    String extractRole(String token);
    boolean isTokenValid(String token);
}
