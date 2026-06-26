package com.digae.sga.entity;

import com.digae.sga.entity.enums.RolUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "administradores")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Administrador extends Usuario {

    @Override
    public RolUsuario getRol() {
        return RolUsuario.ADMINISTRADOR;
    }
}
