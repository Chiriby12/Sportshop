package com.sportshop.auth.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sportshop_users")
@Data
public class UserData {

    @Id
    private String document;

    @Column(nullable = false)
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 15)
    private String telephone;

    private Integer age;

    @Column(length = 10, nullable = false)
    private String role;
}
