package com.digae.sga.dto.request;

import com.digae.sga.entity.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    private String apellido;

    @NotBlank(message = "El email/usuario es requerido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    @NotNull(message = "El rol es requerido")
    private RolUsuario rol;

    private Long facultadId;
}
