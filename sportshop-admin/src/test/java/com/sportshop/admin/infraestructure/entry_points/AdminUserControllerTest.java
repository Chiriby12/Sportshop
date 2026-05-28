package com.sportshop.admin.infraestructure.entry_points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sportshop.admin.application.config.SecurityConfig;
import com.sportshop.admin.application.config.UseCaseConfig;
import com.sportshop.admin.application.dto.AdminUserRequestDTO;
import com.sportshop.admin.application.dto.AdminUserUpdateDTO;
import com.sportshop.admin.domain.model.AdminUser;
import com.sportshop.admin.domain.usecase.AdminUserUseCase;
import com.sportshop.admin.infraestructure.mapper.AdminUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
@Import({SecurityConfig.class, UseCaseConfig.class, AdminUserMapper.class})
@DisplayName("AdminUserController - Tests de endpoints de usuarios")
class AdminUserControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private AdminUserUseCase userUseCase;
    @MockitoBean private AdminUserMapper userMapper;
    @MockitoBean private JwtFilter jwtFilter;

    private ObjectMapper objectMapper;
    private AdminUser user;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        user = new AdminUser("12345678", "Ana García", "ana@test.com", "3001234567", 25, "USER", true);
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /users: crea usuario correctamente")
    void createUser_exitoso() throws Exception {
        AdminUserRequestDTO dto = new AdminUserRequestDTO("12345678","Ana García","ana@test.com","3001234567",25,"USER",true);
        when(userMapper.toDomain(any(AdminUserRequestDTO.class))).thenReturn(user);
        when(userUseCase.createUser(any(), any())).thenReturn(user);

        mockMvc.perform(post("/api/sportshop/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.document").value("12345678"));
    }

    @Test @WithMockUser(roles = "USER")
    @DisplayName("POST /users: USER sin permisos retorna 403")
    void createUser_sinPermisos() throws Exception {
        mockMvc.perform(post("/api/sportshop/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /users: sin token retorna 401")
    void createUser_sinToken() throws Exception {
        mockMvc.perform(post("/api/sportshop/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /users: lista todos los usuarios")
    void getAllUsers_exitoso() throws Exception {
        when(userUseCase.getAllUsers()).thenReturn(List.of(user));
        mockMvc.perform(get("/api/sportshop/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test @WithMockUser(roles = "USER")
    @DisplayName("GET /users: USER sin permisos retorna 403")
    void getAllUsers_sinPermisos() throws Exception {
        mockMvc.perform(get("/api/sportshop/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /users/{document}: retorna usuario existente")
    void getUserByDocument_exitoso() throws Exception {
        when(userUseCase.getUserByDocument("12345678")).thenReturn(user);
        mockMvc.perform(get("/api/sportshop/admin/users/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.document").value("12345678"));
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /users/{document}: no existe retorna 400")
    void getUserByDocument_noExiste() throws Exception {
        when(userUseCase.getUserByDocument("99")).thenThrow(new RuntimeException("No existe"));
        mockMvc.perform(get("/api/sportshop/admin/users/99"))
                .andExpect(status().isBadRequest());
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /users/role/{role}: filtra por rol")
    void getUsersByRole_exitoso() throws Exception {
        when(userUseCase.getUsersByRole("USER")).thenReturn(List.of(user));
        mockMvc.perform(get("/api/sportshop/admin/users/role/USER"))
                .andExpect(status().isOk());
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /users/role/{role}: rol inválido retorna 400")
    void getUsersByRole_invalido() throws Exception {
        when(userUseCase.getUsersByRole("SUPERUSER")).thenThrow(new RuntimeException("Rol inválido"));
        mockMvc.perform(get("/api/sportshop/admin/users/role/SUPERUSER"))
                .andExpect(status().isBadRequest());
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /users/{document}: actualiza usuario correctamente")
    void updateUser_exitoso() throws Exception {
        AdminUserUpdateDTO dto = new AdminUserUpdateDTO("Ana Nueva","3009999999",26,"ADMIN",true);
        when(userMapper.toDomainFromUpdate(eq("12345678"), any())).thenReturn(user);
        when(userUseCase.updateUser(eq("12345678"), any(), any())).thenReturn(user);
        mockMvc.perform(put("/api/sportshop/admin/users/12345678")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /users/{document}: no existe retorna 400")
    void updateUser_noExiste() throws Exception {
        AdminUserUpdateDTO dto = new AdminUserUpdateDTO("Ana Nueva",null,null,null,null);
        when(userMapper.toDomainFromUpdate(eq("99"), any())).thenReturn(user);
        when(userUseCase.updateUser(eq("99"), any(), any())).thenThrow(new RuntimeException("No existe"));
        mockMvc.perform(put("/api/sportshop/admin/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /users/{document}/role: cambia rol correctamente")
    void changeRole_exitoso() throws Exception {
        user.setRole("ADMIN");
        when(userUseCase.changeUserRole(eq("12345678"), eq("ADMIN"), any())).thenReturn(user);
        mockMvc.perform(patch("/api/sportshop/admin/users/12345678/role").param("role","ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /users/{document}/role: rol inválido retorna 400")
    void changeRole_invalido() throws Exception {
        when(userUseCase.changeUserRole(eq("12345678"), eq("GOD"), any()))
                .thenThrow(new RuntimeException("Rol inválido"));
        mockMvc.perform(patch("/api/sportshop/admin/users/12345678/role").param("role","GOD"))
                .andExpect(status().isBadRequest());
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /users/{document}: elimina correctamente")
    void deleteUser_exitoso() throws Exception {
        doNothing().when(userUseCase).deleteUser(eq("12345678"), any());
        mockMvc.perform(delete("/api/sportshop/admin/users/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario 12345678 eliminado correctamente"));
    }

    @Test @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /users/{document}: no existe retorna 400")
    void deleteUser_noExiste() throws Exception {
        doThrow(new RuntimeException("No existe")).when(userUseCase).deleteUser(eq("99"), any());
        mockMvc.perform(delete("/api/sportshop/admin/users/99"))
                .andExpect(status().isBadRequest());
    }

    @Test @WithMockUser(roles = "USER")
    @DisplayName("DELETE /users/{document}: USER sin permisos retorna 403")
    void deleteUser_sinPermisos() throws Exception {
        mockMvc.perform(delete("/api/sportshop/admin/users/12345678"))
                .andExpect(status().isForbidden());
    }
}
