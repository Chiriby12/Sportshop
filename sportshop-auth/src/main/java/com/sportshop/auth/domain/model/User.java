package com.sportshop.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String document;
    private String name;
    private String email;
    private String password;
    private String telephone;
    private Integer age;
    private String role; // USER | ADMIN
}
