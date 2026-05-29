package com.sportshop.auth.infraestructure.mapper;

import com.sportshop.auth.application.dto.UserRequestDTO;
import com.sportshop.auth.application.dto.UserResponseDTO;
import com.sportshop.auth.application.dto.UserUpdateDTO;
import com.sportshop.auth.domain.model.User;
import com.sportshop.auth.infraestructure.driver_adapters.jpa_repository.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
    }

    @Test
    @DisplayName("toUserData: convierte User a UserData correctamente")
    void toUserData() {
        User user = new User("123", "Juan", "j@mail.com", "hash", "300", 25, "USER");
        UserData result = mapper.toUserData(user);
        assertEquals("123", result.getDocument());
        assertEquals("Juan", result.getName());
        assertEquals("j@mail.com", result.getEmail());
    }

    @Test
    @DisplayName("toUser: convierte UserData a User correctamente")
    void toUser() {
        UserData data = new UserData("123", "Juan", "j@mail.com", "hash", "300", 25, "USER");
        User result = mapper.toUser(data);
        assertEquals("123", result.getDocument());
        assertEquals("USER", result.getRole());
    }

    @Test
    @DisplayName("toUserFromDTO: convierte UserRequestDTO a User")
    void toUserFromDTO() {
        UserRequestDTO dto = new UserRequestDTO("123", "Juan", "j@mail.com", "clave", "300", 25, "USER");
        User result = mapper.toUserFromDTO(dto);
        assertEquals("j@mail.com", result.getEmail());
        assertEquals("clave", result.getPassword());
    }

    @Test
    @DisplayName("toUserFromUpdateDTO: convierte UserUpdateDTO a User")
    void toUserFromUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO("123", "Juan", "j@mail.com", "nuevaClave", "300", 26, "ADMIN");
        User result = mapper.toUserFromUpdateDTO(dto);
        assertEquals("ADMIN", result.getRole());
        assertEquals(26, result.getAge());
    }

    @Test
    @DisplayName("toUserResponseDTO: convierte User a DTO de respuesta sin password")
    void toUserResponseDTO() {
        User user = new User("123", "Juan", "j@mail.com", "secretHash", "300", 25, "USER");
        UserResponseDTO result = mapper.toUserResponseDTO(user);
        assertEquals("123", result.getDocument());
        assertEquals("Juan", result.getName());

        assertNull(result.getClass().getDeclaredFields()[0].getAnnotation(
                com.fasterxml.jackson.annotation.JsonProperty.class));
    }
}
