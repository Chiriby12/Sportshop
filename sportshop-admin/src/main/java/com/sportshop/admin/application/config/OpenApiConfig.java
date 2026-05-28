package com.sportshop.admin.application.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SportShop - Microservicio Admin",
                version = "1.0",
                description = "Panel de administración: CRUD completo de usuarios y productos. " +
                        "Todos los endpoints requieren token JWT con rol ADMIN. " +
                        "Obtén el token en el microservicio de auth (puerto 8080)."
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
