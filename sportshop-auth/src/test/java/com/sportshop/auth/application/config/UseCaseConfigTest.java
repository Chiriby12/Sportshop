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
        // 1. Instanciamos la clase de configuración de forma manual (como una clase normal)
        UseCaseConfig config = new UseCaseConfig();

        // 2. Creamos los mocks manuales con Mockito estándar
        UserGateway userGateway = mock(UserGateway.class);
        EncrypterGateway encrypterGateway = mock(EncrypterGateway.class);
        JwtGateway jwtGateway = mock(JwtGateway.class);

        // 3. Llamamos al método @Bean directamente
        UserUseCase result = config.userUseCase(userGateway, encrypterGateway, jwtGateway);

        // 4. Verificamos que no sea nulo
        assertNotNull(result, "El método userUseCase debería retornar una instancia válida");
    }
}