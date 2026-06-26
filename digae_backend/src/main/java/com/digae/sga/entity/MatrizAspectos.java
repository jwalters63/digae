package com.digae.sga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "matrices_aspectos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatrizAspectos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fechaEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facultad_id", nullable = false)
    private Facultad facultad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por", nullable = false)
    private Administrador creadoPor;

    @OneToMany(mappedBy = "matriz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AspectAmbiental> aspectos;
}
