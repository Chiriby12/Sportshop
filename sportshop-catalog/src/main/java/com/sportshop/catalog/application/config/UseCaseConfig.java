package com.sportshop.catalog.application.config;

import com.sportshop.catalog.domain.model.gateway.CartGateway;
import com.sportshop.catalog.domain.model.gateway.EventPublisherGateway;
import com.sportshop.catalog.domain.model.gateway.ProductGateway;
import com.sportshop.catalog.domain.usecase.CartUseCase;
import com.sportshop.catalog.domain.usecase.ProductUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class UseCaseConfig {

    @Bean
    public ProductUseCase productUseCase(ProductGateway productGateway,
                                          EventPublisherGateway eventPublisher) {
        return new ProductUseCase(productGateway, eventPublisher);
    }

    @Bean
    public CartUseCase cartUseCase(CartGateway cartGateway,
                                    ProductGateway productGateway,
                                    EventPublisherGateway eventPublisher) {
        return new CartUseCase(cartGateway, productGateway, eventPublisher);
    }
}
