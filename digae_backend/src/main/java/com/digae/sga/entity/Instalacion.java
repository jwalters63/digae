package com.digae.sga.entity;

import com.digae.sga.entity.enums.TipoInstalacion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instalaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instalacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoInstalacion tipo;

    @Column(nullable = false, length = 255)
    private String ubicacion;
}
