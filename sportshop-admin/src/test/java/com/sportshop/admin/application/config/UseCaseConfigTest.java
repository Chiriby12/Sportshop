package com.sportshop.admin.application.config;

import com.sportshop.admin.domain.model.gateway.AdminProductGateway;
import com.sportshop.admin.domain.model.gateway.AdminUserGateway;
import com.sportshop.admin.domain.model.gateway.CatalogSyncGateway;
import com.sportshop.admin.domain.model.gateway.EventPublisherGateway;
import com.sportshop.admin.domain.usecase.AdminProductUseCase;
import com.sportshop.admin.domain.usecase.AdminUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UseCaseConfigTest {

    @Autowired
    private AdminUserUseCase adminUserUseCase;

    @Autowired
    private AdminProductUseCase adminProductUseCase;

    @MockBean
    private AdminUserGateway adminUserGateway;

    @MockBean
    private AdminProductGateway adminProductGateway;

    @MockBean
    private EventPublisherGateway eventPublisherGateway;

    @MockBean
    private CatalogSyncGateway catalogSyncGateway;

    @Test
    void adminUserUseCase_isCreatedAsBean() {
        assertThat(adminUserUseCase).isNotNull();
    }

    @Test
    void adminProductUseCase_isCreatedAsBean() {
        assertThat(adminProductUseCase).isNotNull();
    }

    @Test
    void adminUserUseCase_isInstanceOfCorrectType() {
        assertThat(adminUserUseCase).isInstanceOf(AdminUserUseCase.class);
    }

    @Test
    void adminProductUseCase_isInstanceOfCorrectType() {
        assertThat(adminProductUseCase).isInstanceOf(AdminProductUseCase.class);
    }
}