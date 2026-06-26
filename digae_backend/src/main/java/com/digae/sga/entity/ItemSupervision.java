package com.digae.sga.entity;

import com.digae.sga.entity.enums.ResultadoSupervision;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_supervision")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemSupervision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String categoria;

    @Column(nullable = false, length = 250)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoSupervision resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervision_id", nullable = false, 
            foreignKey = @ForeignKey(name = "fk_item_supervision"))
    private Supervision supervision;
}
