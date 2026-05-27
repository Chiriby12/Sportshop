package com.sportshop.admin.infraestructure.mapper;

import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.infraestructure.driver_adapters.jpa_repository.AdminProductData;
import org.springframework.stereotype.Component;

@Component
public class AdminProductMapper {

    public AdminProduct toDomain(AdminProductData data) {
        if (data == null) return null;
        return new AdminProduct(
                data.getId(), data.getName(), data.getDescription(),
                data.getBrand(), data.getCategory(), data.getSport(),
                data.getPrice(), data.getStock(), data.getImageUrl(), data.getActive()
        );
    }

    public AdminProductData toData(AdminProduct domain) {
        if (domain == null) return null;
        return new AdminProductData(
                domain.getId(), domain.getName(), domain.getDescription(),
                domain.getBrand(), domain.getCategory(), domain.getSport(),
                domain.getPrice(), domain.getStock(), domain.getImageUrl(),
                domain.getActive() != null ? domain.getActive() : true
        );
    }
}
