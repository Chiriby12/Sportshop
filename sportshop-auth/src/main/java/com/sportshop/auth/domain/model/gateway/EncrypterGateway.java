package com.sportshop.auth.domain.model.gateway;

public interface EncrypterGateway {
    String encrypt(String raw);
    boolean matches(String raw, String hashed);
}
