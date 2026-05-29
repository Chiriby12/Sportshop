package com.sportshop.admin.application.config;

import com.sportshop.admin.domain.model.gateway.AdminProductGateway;
import com.sportshop.admin.domain.model.gateway.AdminUserGateway;
import com.sportshop.admin.domain.model.gateway.CatalogSyncGateway;
import com.sportshop.admin.domain.model.gateway.EventPublisherGateway;
import com.sportshop.admin.domain.usecase.AdminProductUseCase;
import com.sportshop.admin.domain.usecase.AdminUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class UseCaseConfig {

    @Bean
    public AdminUserUseCase adminUserUseCase(AdminUserGateway userGateway,
                                             EventPublisherGateway eventPublisher) {
        return new AdminUserUseCase(userGateway, eventPublisher);
    }

    @Bean
    public AdminProductUseCase adminProductUseCase(AdminProductGateway productGateway,
                                                   EventPublisherGateway eventPublisher,
                                                   CatalogSyncGateway catalogSync) {
        return new AdminProductUseCase(productGateway, eventPublisher, catalogSync);
    }
}