package com.sportshop.admin.infraestructure.mapper;

import com.sportshop.admin.application.dto.AdminUserRequestDTO;
import com.sportshop.admin.application.dto.AdminUserUpdateDTO;
import com.sportshop.admin.domain.model.AdminUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminUserMapper - Tests")
class AdminUserMapperTest {

    private AdminUserMapper mapper;

    @BeforeEach
    void setUp() { mapper = new AdminUserMapper(); }

    @Test
    @DisplayName("toDomain(RequestDTO): mapea todos los campos correctamente")
    void toDomain_requestDTO_ok() {
        AdminUserRequestDTO dto = new AdminUserRequestDTO(
                "12345678", "Ana García", "ana@test.com", "3001234567", 25, "USER", true
        );
        AdminUser result = mapper.toDomain(dto);
        assertNotNull(result);
        assertEquals("12345678", result.getDocument());
        assertEquals("Ana García", result.getName());
        assertEquals("ana@test.com", result.getEmail());
        assertEquals("3001234567", result.getTelephone());
        assertEquals(25, result.getAge());
        assertEquals("USER", result.getRole());
        assertTrue(result.getActive());
    }

    @Test
    @DisplayName("toDomain(RequestDTO): role null asigna USER por defecto")
    void toDomain_roleNull_asignaUSER() {
        AdminUserRequestDTO dto = new AdminUserRequestDTO(
                "123", "Juan", "j@test.com", null, null, null, null
        );
        AdminUser result = mapper.toDomain(dto);
        assertEquals("USER", result.getRole());
        assertTrue(result.getActive());
    }

    @Test
    @DisplayName("toDomain(RequestDTO): null retorna null")
    void toDomain_requestDTO_null() {
        assertNull(mapper.toDomain((AdminUserRequestDTO) null));
    }

    @Test
    @DisplayName("toDomainFromUpdate: mapea campos de actualización")
    void toDomainFromUpdate_ok() {
        AdminUserUpdateDTO dto = new AdminUserUpdateDTO("Ana Nueva", "3009999999", 26, "ADMIN", false);
        AdminUser result = mapper.toDomainFromUpdate("12345678", dto);
        assertNotNull(result);
        assertEquals("12345678", result.getDocument());
        assertEquals("Ana Nueva", result.getName());
        assertEquals("3009999999", result.getTelephone());
        assertEquals(26, result.getAge());
        assertEquals("ADMIN", result.getRole());
        assertFalse(result.getActive());
    }

    @Test
    @DisplayName("toDomainFromUpdate: null retorna null")
    void toDomainFromUpdate_null() {
        assertNull(mapper.toDomainFromUpdate("123", null));
    }

    @Test
    @DisplayName("toDomainFromUpdate: campos opcionales null se conservan null")
    void toDomainFromUpdate_camposNull() {
        AdminUserUpdateDTO dto = new AdminUserUpdateDTO("Solo nombre", null, null, null, null);
        AdminUser result = mapper.toDomainFromUpdate("99", dto);
        assertEquals("Solo nombre", result.getName());
        assertNull(result.getTelephone());
        assertNull(result.getAge());
        assertNull(result.getRole());
    }
}
