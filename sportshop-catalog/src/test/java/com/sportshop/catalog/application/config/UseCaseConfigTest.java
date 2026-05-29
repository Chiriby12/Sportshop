package com.sportshop.catalog.application.config;

import com.sportshop.catalog.domain.model.gateway.CartGateway;
import com.sportshop.catalog.domain.model.gateway.EventPublisherGateway;
import com.sportshop.catalog.domain.model.gateway.ProductGateway;
import com.sportshop.catalog.domain.usecase.CartUseCase;
import com.sportshop.catalog.domain.usecase.ProductUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("UseCaseConfig - Test de Cobertura Total")
class UseCaseConfigTest {


    @Mock
    private ProductGateway productGateway;

    @Mock
    private CartGateway cartGateway;

    @Mock
    private EventPublisherGateway eventPublisher;


    @InjectMocks
    private UseCaseConfig useCaseConfig;

    @Test
    @DisplayName("Debe instanciar ProductUseCase correctamente")
    void productUseCase_debeRetornarInstancia() {

        ProductUseCase productUseCase = useCaseConfig.productUseCase(productGateway, eventPublisher);


        assertNotNull(productUseCase, "La instancia de ProductUseCase no debe ser nula");
    }

    @Test
    @DisplayName("Debe instanciar CartUseCase correctamente")
    void cartUseCase_debeRetornarInstancia() {

        CartUseCase cartUseCase = useCaseConfig.cartUseCase(cartGateway, productGateway, eventPublisher);


        assertNotNull(cartUseCase, "La instancia de CartUseCase no debe ser nula");
    }
}