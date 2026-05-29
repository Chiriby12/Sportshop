package com.sportshop.auth.infraestructure.security_encrypter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.sportshop.auth.domain.model.gateway.EncrypterGateway;
import org.springframework.stereotype.Service;


@Service
public class EncrypterGatewayImpl implements EncrypterGateway {

    @Override
    public String encrypt(String raw) {
        return BCrypt.withDefaults().hashToString(12, raw.toCharArray());
    }

    @Override
    public boolean matches(String raw, String hashed) {
        return BCrypt.verifyer().verify(raw.toCharArray(), hashed).verified;
    }
}
