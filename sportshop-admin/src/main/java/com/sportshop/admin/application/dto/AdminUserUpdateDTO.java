package com.sportshop.admin.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AdminUserUpdateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String telephone;

    @Min(value = 1, message = "La edad debe ser mayor a 0")
    @Max(value = 120, message = "La edad no puede ser mayor a 120")
    private Integer age;

    @Pattern(regexp = "^(USER|ADMIN)$", message = "El rol debe ser USER o ADMIN")
    private String role;

    private Boolean active;
}
