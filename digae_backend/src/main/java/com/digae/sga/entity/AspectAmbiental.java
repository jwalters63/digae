package com.digae.sga.entity;

import com.digae.sga.entity.enums.NivelCriticidad;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.List;

@Entity
@Table(name = "aspectos_ambientales")
@Check(constraints = "gravedad BETWEEN 1 AND 5 AND severidad BETWEEN 1 AND 5 AND probabilidad BETWEEN 1 AND 5")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AspectAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matriz_id", nullable = false, 
            foreignKey = @ForeignKey(name = "fk_aspecto_matriz"))
    private MatrizAspectos matriz;

    @Column(nullable = false)
    private Integer gravedad;

    @Column(nullable = false)
    private Integer severidad;

    @Column(nullable = false)
    private Integer probabilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_criticidad", length = 20)
    private NivelCriticidad nivelCriticidad;

    @ManyToMany
    @JoinTable(
        name = "aspecto_control",
        joinColumns = @JoinColumn(name = "aspecto_id", foreignKey = @ForeignKey(name = "fk_ac_aspecto")),
        inverseJoinColumns = @JoinColumn(name = "control_id", foreignKey = @ForeignKey(name = "fk_ac_control"))
    )
    private List<ControlOperacional> controles;
}
