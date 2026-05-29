package com.sportshop.admin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


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
    private String role;
    private Boolean active;
}
