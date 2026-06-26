package com.digae.sga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "facultades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150, unique = true)
    private String nombre;

    @Column(length = 20)
    private String siglas;

    @OneToMany(mappedBy = "facultad")
    private List<UsuarioOperativo> coordinadores;
}
