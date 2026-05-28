package com.sportshop.admin.domain.model.gateway;

import com.sportshop.admin.domain.model.AdminProduct;

public interface CatalogSyncGateway {
    void createOrUpdate(AdminProduct product);
    void delete(Long productId);
}