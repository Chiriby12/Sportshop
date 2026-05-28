package com.sportshop.catalog.infraestructure.mapper;

import com.sportshop.catalog.application.dto.ProductRequestDTO;
import com.sportshop.catalog.application.dto.ProductResponseDTO;
import com.sportshop.catalog.domain.model.Product;
import com.sportshop.catalog.infraestructure.driver_adapters.jpa_repository.ProductData;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toDomain(ProductData data) {
        if (data == null) return null;
        return new Product(
                data.getId(), data.getAdminId(), data.getName(), data.getDescription(),
                data.getBrand(), data.getCategory(), data.getSport(),
                data.getPrice(), data.getStock(), data.getImageUrl(), data.getActive()
        );
    }

    public ProductData toData(Product domain) {
        if (domain == null) return null;
        return new ProductData(
                domain.getId(), domain.getAdminId(), domain.getName(), domain.getDescription(),
                domain.getBrand(), domain.getCategory(), domain.getSport(),
                domain.getPrice(), domain.getStock(), domain.getImageUrl(),
                domain.getActive() != null ? domain.getActive() : true
        );
    }

    public Product fromRequestDTO(ProductRequestDTO dto) {
        if (dto == null) return null;
        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setBrand(dto.getBrand());
        p.setCategory(dto.getCategory());
        p.setSport(dto.getSport());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setImageUrl(dto.getImageUrl());
        p.setActive(dto.getActive() != null ? dto.getActive() : true);
        return p;
    }

    public ProductResponseDTO toResponseDTO(Product domain) {
        if (domain == null) return null;
        return new ProductResponseDTO(
                domain.getId(), domain.getName(), domain.getDescription(),
                domain.getBrand(), domain.getCategory(), domain.getSport(),
                domain.getPrice(), domain.getStock(), domain.getImageUrl(), domain.getActive()
        );
    }
}