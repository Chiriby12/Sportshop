package com.sportshop.auth.application.config;

import com.sportshop.auth.domain.model.gateway.EncrypterGateway;
import com.sportshop.auth.domain.model.gateway.JwtGateway;
import com.sportshop.auth.domain.model.gateway.UserGateway;
import com.sportshop.auth.domain.usecase.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de casos de uso.
 * Arquitectura Hexagonal: aquí conectamos los puertos con los adaptadores.
 * El UseCase (dominio) no sabe nada de Spring; es este config quien inyecta
 * los adaptadores concretos a través de las interfaces/gateways.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public UserUseCase userUseCase(UserGateway userGateway,
                                   EncrypterGateway encrypterGateway,
                                   JwtGateway jwtGateway) {
        return new UserUseCase(userGateway, encrypterGateway, jwtGateway);
    }
}
