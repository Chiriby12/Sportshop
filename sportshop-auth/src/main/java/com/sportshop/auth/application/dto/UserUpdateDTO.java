package com.sportshop.auth.application.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @NotBlank(message = "El documento es obligatorio")
    @Pattern(
        regexp = "^[0-9]{8,10}$",
        message = "El documento debe tener entre 8 y 10 dígitos numéricos"
    )
    private String document;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(
        regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
        message = "El nombre solo puede contener letras y espacios"
    )
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    // Opcional en update — si se envía, debe cumplir el patrón
    @Pattern(
        regexp = "^$|^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,20}$",
        message = "La contraseña debe tener entre 8 y 20 caracteres, al menos una mayúscula, un número y un carácter especial (!@#$%)"
    )
    private String password;

    @Pattern(
        regexp = "^[0-9]{7,10}$",
        message = "El teléfono debe tener entre 7 y 10 dígitos numéricos"
    )
    private String telephone;

    @Min(value = 18, message = "Debes tener mínimo 18 años")
    @Max(value = 100, message = "Edad no válida")
    private Integer age;

    @Pattern(
        regexp = "^(USER|ADMIN)$",
        message = "El rol debe ser USER o ADMIN"
    )
    private String role;
}
