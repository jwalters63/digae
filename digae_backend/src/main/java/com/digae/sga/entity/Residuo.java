package com.digae.sga.entity;

import com.digae.sga.entity.enums.ClasificacionResiduo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "residuos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Residuo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClasificacionResiduo clasificacion;

    @Column(name = "peso_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "volumen_m3", precision = 10, scale = 2)
    private BigDecimal volumenM3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bitacora_id", nullable = false, 
            foreignKey = @ForeignKey(name = "fk_residuo_bitacora"))
    private BitacoraResiduo bitacora;
}
