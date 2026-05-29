package com.sportshop.auth.infraestructure.entry_points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshop.auth.application.config.SecurityConfig;
import com.sportshop.auth.application.config.UseCaseConfig;
import com.sportshop.auth.application.dto.*;
import com.sportshop.auth.domain.model.User;
import com.sportshop.auth.domain.model.gateway.UserGateway;
import com.sportshop.auth.domain.usecase.UserUseCase;
import com.sportshop.auth.infraestructure.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({SecurityConfig.class, UseCaseConfig.class, UserMapper.class})
@DisplayName("UserController - Tests de endpoints de autenticación")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserUseCase userUseCase;
    @MockitoBean
    private UserMapper userMapper;
    @MockitoBean
    private UserGateway userGateway;
    @MockitoBean
    private JwtFilter jwtFilter;
    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserRequestDTO validDTO;

    @BeforeEach
    void setUp() {
        user = new User("12345678", "Ana García", "ana@test.com", "hashedPass", "3001234567", 25, "USER");
        validDTO = new UserRequestDTO("12345678", "Ana García", "ana@test.com",
                "Clave123!", "3001234567", 25, "USER");
    }



    @Test
    @DisplayName("POST /save: registra usuario con datos válidos")
    void save_exitoso() throws Exception {
        when(userMapper.toUserFromDTO(any())).thenReturn(user);
        when(userUseCase.saveUser(any())).thenReturn(user);
        when(userMapper.toUserResponseDTO(any())).thenReturn(
                new UserResponseDTO("12345678", "Ana García", "ana@test.com", "3001234567", 25, "USER"));

        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.document").value("12345678"));
    }

    @Test
    @DisplayName("POST /save: contraseña sin mayúscula retorna 400")
    void save_passwordSinMayuscula() throws Exception {
        validDTO.setPassword("clave123!");
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: contraseña sin número retorna 400")
    void save_passwordSinNumero() throws Exception {
        validDTO.setPassword("ClaveABC!");
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: contraseña sin especial retorna 400")
    void save_passwordSinEspecial() throws Exception {
        validDTO.setPassword("Clave1234");
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: contraseña muy corta retorna 400")
    void save_passwordCorta() throws Exception {
        validDTO.setPassword("C1!");
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: documento con letras retorna 400")
    void save_documentoConLetras() throws Exception {
        validDTO.setDocument("ABC12345");
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: documento muy corto retorna 400")
    void save_documentoCorto() throws Exception {
        validDTO.setDocument("1234");
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: email inválido retorna 400")
    void save_emailInvalido() throws Exception {
        validDTO.setEmail("no-es-email");
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: nombre con números retorna 400")
    void save_nombreConNumeros() throws Exception {
        validDTO.setName("Ana123");
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: edad menor de 18 retorna 400")
    void save_edadMenorDe18() throws Exception {
        validDTO.setAge(15);
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: rol inválido retorna 400")
    void save_rolInvalido() throws Exception {
        validDTO.setRole("SUPERUSER");
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /save: múltiples errores retorna todos los campos fallidos")
    void save_multiplesErrores() throws Exception {
        validDTO.setPassword("abc");
        validDTO.setEmail("malemail");
        validDTO.setAge(10);
        mockMvc.perform(post("/api/sportshop/auth/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data", aMapWithSize(greaterThanOrEqualTo(2))));
    }


    @Test
    @DisplayName("POST /login: login exitoso retorna token")
    void login_exitoso() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("ana@test.com", "Clave123!");
        when(userUseCase.loginUser("ana@test.com", "Clave123!")).thenReturn("jwt.token");
        when(userGateway.getUserByEmail("ana@test.com")).thenReturn(user);
        when(userMapper.toUserResponseDTO(any())).thenReturn(
                new UserResponseDTO("12345678", "Ana García", "ana@test.com", "3001234567", 25, "USER"));

        mockMvc.perform(post("/api/sportshop/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt.token"));
    }

    @Test
    @DisplayName("POST /login: email vacío retorna 400")
    void login_emailVacio() throws Exception {

        mockMvc.perform(post("/api/sportshop/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"Clave123!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /login: contraseña incorrecta retorna 401")
    void login_passIncorrecta() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("ana@test.com", "WrongPass");
        when(userUseCase.loginUser(any(), any()))
                .thenThrow(new RuntimeException("Contraseña incorrecta"));

        mockMvc.perform(post("/api/sportshop/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Contraseña incorrecta"));
    }

    @Test
    @DisplayName("POST /login: usuario no existe retorna 404")
    void login_noExiste() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("noexiste@test.com", "Clave123!");
        when(userUseCase.loginUser(any(), any()))
                .thenThrow(new RuntimeException("No existe un usuario con el email: noexiste@test.com"));

        mockMvc.perform(post("/api/sportshop/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isNotFound());
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /get/{document}: ADMIN obtiene usuario")
    void getUser_admin() throws Exception {
        when(userUseCase.getUserForDocument("12345678")).thenReturn(user);
        when(userMapper.toUserResponseDTO(any())).thenReturn(
                new UserResponseDTO("12345678", "Ana García", "ana@test.com", "3001234567", 25, "USER"));

        mockMvc.perform(get("/api/sportshop/auth/get/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.document").value("12345678"));
    }

    @Test
    @DisplayName("GET /get/{document}: sin token retorna 404 por excepción de dominio")
    void getUser_sinToken() throws Exception {

        when(userUseCase.getUserForDocument(any()))
                .thenThrow(new RuntimeException("No existe un usuario con el documento: 12345678"));

        mockMvc.perform(get("/api/sportshop/auth/get/12345678"))
                .andExpect(status().isNotFound());
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /delete/{document}: ADMIN elimina usuario")
    void deleteUser_admin() throws Exception {
        doNothing().when(userUseCase).deleteUserForDocument("12345678");

        mockMvc.perform(delete("/api/sportshop/auth/delete/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value(containsString("eliminado")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /delete/{document}: USER sin permisos retorna 403")
    void deleteUser_sinPermisos() throws Exception {

        doThrow(new RuntimeException("No tienes permiso"))
                .when(userUseCase).deleteUserForDocument(any());

        mockMvc.perform(delete("/api/sportshop/auth/delete/12345678"))
                .andExpect(status().isForbidden());
    }
}