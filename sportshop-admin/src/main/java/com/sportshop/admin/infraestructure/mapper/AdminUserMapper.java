package com.sportshop.admin.infraestructure.mapper;

import com.sportshop.admin.application.dto.AdminUserRequestDTO;
import com.sportshop.admin.application.dto.AdminUserUpdateDTO;
import com.sportshop.admin.domain.model.AdminUser;
import org.springframework.stereotype.Component;

/**
 * Mapper entre DTOs y el modelo de dominio AdminUser.
 * Ya no necesita mapear a/desde JPA entity porque la persistencia
 * la maneja sportshop-auth via HTTP.
 */
@Component
public class AdminUserMapper {

    public AdminUser toDomain(AdminUserRequestDTO dto) {
        if (dto == null) return null;
        AdminUser user = new AdminUser();
        user.setDocument(dto.getDocument());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setTelephone(dto.getTelephone());
        user.setAge(dto.getAge());
        user.setRole(dto.getRole() != null ? dto.getRole() : "USER");
        user.setActive(dto.getActive() != null ? dto.getActive() : true);
        return user;
    }

    public AdminUser toDomainFromUpdate(String document, AdminUserUpdateDTO dto) {
        if (dto == null) return null;
        AdminUser user = new AdminUser();
        user.setDocument(document);
        user.setName(dto.getName());
        user.setTelephone(dto.getTelephone());
        user.setAge(dto.getAge());
        user.setRole(dto.getRole());
        user.setActive(dto.getActive());
        return user;
    }
}
