package com.sportshop.auth.infraestructure.mapper;

import com.sportshop.auth.application.dto.UserRequestDTO;
import com.sportshop.auth.application.dto.UserResponseDTO;
import com.sportshop.auth.application.dto.UserUpdateDTO;
import com.sportshop.auth.domain.model.User;
import com.sportshop.auth.infraestructure.driver_adapters.jpa_repository.UserData;
import org.springframework.stereotype.Component;

/**
 * Mapper de infraestructura.
 * Traduce entre: DTO <-> Dominio <-> Entidad JPA
 * El dominio nunca conoce ni los DTOs ni las entidades JPA.
 */
@Component
public class UserMapper {

    public UserData toUserData(User user) {
        return new UserData(
                user.getDocument(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getTelephone(),
                user.getAge(),
                user.getRole()
        );
    }

    public User toUser(UserData userData) {
        return new User(
                userData.getDocument(),
                userData.getName(),
                userData.getEmail(),
                userData.getPassword(),
                userData.getTelephone(),
                userData.getAge(),
                userData.getRole()
        );
    }

    public User toUserFromDTO(UserRequestDTO dto) {
        return new User(
                dto.getDocument(),
                dto.getName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getTelephone(),
                dto.getAge(),
                dto.getRole()
        );
    }

    public User toUserFromUpdateDTO(UserUpdateDTO dto) {
        return new User(
                dto.getDocument(),
                dto.getName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getTelephone(),
                dto.getAge(),
                dto.getRole()
        );
    }

    public UserResponseDTO toUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getDocument(),
                user.getName(),
                user.getEmail(),
                user.getTelephone(),
                user.getAge(),
                user.getRole()
        );
    }
}
