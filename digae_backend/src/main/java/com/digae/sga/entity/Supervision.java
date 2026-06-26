package com.digae.sga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un formulario de supervisión/auditoría de campo.
 * Evalúa criterios de infraestructura, manejo de residuos y eficiencia operativa.
 */
@Entity
@Table(name = "supervisiones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supervision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_inspeccion", nullable = false)
    private LocalDate fechaInspeccion;

    @Column(name = "area_inspeccionada", nullable = false, length = 200)
    private String areaInspeccionada;

    @OneToMany(mappedBy = "supervision", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ItemSupervision> items;

    @Column(name = "calificacion_general", nullable = false, precision = 4, scale = 2)
    private BigDecimal calificacionGeneral;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_supervision_inspector"))
    private Usuario inspector;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        if (this.fechaInspeccion == null) {
            this.fechaInspeccion = LocalDate.now();
        }
    }
}
