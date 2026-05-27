package com.sportshop.admin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad de dominio - Usuario gestionado por el Admin.
 * Núcleo del hexágono. Sin Spring, sin JPA.
 *
 * El admin puede ver y modificar todos los usuarios del sistema.
 * Los datos se sincronizan desde el microservicio de auth vía endpoint,
 * o se gestionan localmente como copia de solo lectura/escritura admin.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUser {
    private String document;
    private String name;
    private String email;
    private String telephone;
    private Integer age;
    private String role;      // USER | ADMIN
    private Boolean active;
}
