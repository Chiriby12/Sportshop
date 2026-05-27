package com.sportshop.catalog.infraestructure.mapper;

import com.sportshop.catalog.application.dto.CartResponseDTO;
import com.sportshop.catalog.domain.model.CartItem;
import com.sportshop.catalog.infraestructure.driver_adapters.jpa_repository.CartItemData;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public CartItem toDomain(CartItemData data) {
        if (data == null) return null;
        return new CartItem(
                data.getId(), data.getUserDocument(), data.getProductId(),
                data.getProductName(), data.getUnitPrice(), data.getQuantity()
        );
    }

    public CartItemData toData(CartItem domain) {
        if (domain == null) return null;
        return new CartItemData(
                domain.getId(), domain.getUserDocument(), domain.getProductId(),
                domain.getProductName(), domain.getUnitPrice(), domain.getQuantity()
        );
    }

    public CartResponseDTO toResponseDTO(CartItem domain) {
        if (domain == null) return null;
        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(domain.getId());
        dto.setUserDocument(domain.getUserDocument());
        dto.setProductId(domain.getProductId());
        dto.setProductName(domain.getProductName());
        dto.setUnitPrice(domain.getUnitPrice());
        dto.setQuantity(domain.getQuantity());
        return dto;
    }
}
