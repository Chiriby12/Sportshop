package com.sportshop.auth.application.config;

import com.sportshop.auth.domain.model.gateway.EncrypterGateway;
import com.sportshop.auth.domain.model.gateway.JwtGateway;
import com.sportshop.auth.domain.model.gateway.UserGateway;
import com.sportshop.auth.domain.usecase.UserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@DisplayName("UseCaseConfig - Test de Cobertura Unitario")
class UseCaseConfigTest {

    @Test
    @DisplayName("Debe retornar la instancia de UserUseCase al invocar el método directamente")
    void shouldReturnUserUseCaseInstance() {

        UseCaseConfig config = new UseCaseConfig();


        UserGateway userGateway = mock(UserGateway.class);
        EncrypterGateway encrypterGateway = mock(EncrypterGateway.class);
        JwtGateway jwtGateway = mock(JwtGateway.class);


        UserUseCase result = config.userUseCase(userGateway, encrypterGateway, jwtGateway);


        assertNotNull(result, "El método userUseCase debería retornar una instancia válida");
    }
}