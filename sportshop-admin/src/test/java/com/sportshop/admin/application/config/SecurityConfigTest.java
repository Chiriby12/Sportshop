package com.sportshop.admin.application.config;

import com.sportshop.admin.domain.usecase.AdminProductUseCase;
import com.sportshop.admin.domain.usecase.AdminUserUseCase;
import com.sportshop.admin.infraestructure.entry_points.AdminProductController;
import com.sportshop.admin.infraestructure.entry_points.AdminUserController;
import com.sportshop.admin.infraestructure.entry_points.GlobalExceptionHandler;
import com.sportshop.admin.infraestructure.entry_points.JwtFilter;
import com.sportshop.admin.infraestructure.mapper.AdminProductMapper;
import com.sportshop.admin.infraestructure.mapper.AdminUserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SecurityConfig — Tests de seguridad del admin")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private AdminProductUseCase productUseCase;
    @MockitoBean private AdminUserUseCase userUseCase;
    @MockitoBean private AdminProductMapper productMapper;
    @MockitoBean private AdminUserMapper userMapper;

    @Test
    @DisplayName("GET /api-docs es accesible sin token")
    void apiDocs_publico() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Swagger UI es accesible sin token")
    void swagger_publico() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("GET /api/sportshop/admin/users sin token retorna 401")
    void usuarios_sinToken_401() throws Exception {
        mockMvc.perform(get("/api/sportshop/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/sportshop/admin/products sin token retorna 401")
    void productos_sinToken_401() throws Exception {
        mockMvc.perform(get("/api/sportshop/admin/products"))
                .andExpect(status().isUnauthorized());
    }
}