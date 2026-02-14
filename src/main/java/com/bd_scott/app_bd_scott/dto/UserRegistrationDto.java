package com.bd_scott.app_bd_scott.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {

    @NotEmpty(message = "Nombre obligatorio")
    @Size(max = 45, min = 3, message = "Mínimo 3 y máximo 45 caracteres")
    private String name;

    @NotEmpty(message = "Username obligatorio")
    @Size(max = 45, min = 3, message = "Mínimo 3 y máximo 45 caracteres")
    private String username;

    @Email(message = "Email inválido")
    @NotEmpty(message = "Email obligatorio")
    @Size(max = 100, min = 5, message = "Mínimo 5 y máximo 100 caracteres")
    private String email;

    @NotEmpty(message = "Password obligatorio")
    @Size(max = 100, min = 5, message = "Mínimo 5 y máximo 100 caracteres")
    private String password;

    @NotEmpty(message = "Confirmar el password es obligatorio")
    @Size(max = 100, min = 5, message = "Mínimo 5 y máximo 100 caracteres")
    private String confirmPassword;

}
