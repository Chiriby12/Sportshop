package com.sportshop.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private String document;
    private String name;
    private String email;
    private String telephone;
    private Integer age;
    private String role;
}
