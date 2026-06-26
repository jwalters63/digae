package com.digae.sga.entity;

import com.digae.sga.entity.enums.ClasificacionResiduo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un registro en la bitácora de residuos sólidos.
 * Incluye clasificación, peso, área generadora, empresa recolectora y firma digital.
 */
@Entity
@Table(name = "bitacora_residuos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BitacoraResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @OneToMany(mappedBy = "bitacora", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Residuo> residuos;

    @Column(name = "area_generadora", nullable = false, length = 200)
    private String areaGeneradora;

    @Column(name = "empresa_recolectora", nullable = false, length = 200)
    private String empresaRecolectora;

    @Column(name = "firma_base64", columnDefinition = "TEXT")
    private String firmaBase64;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bitacora_usuario"))
    private Usuario usuario;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
    }
}
