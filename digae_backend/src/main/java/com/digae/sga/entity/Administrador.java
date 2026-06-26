package com.digae.sga.entity;

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
    // El administrador tiene acceso global, no requiere estar atado a una facultad
}
