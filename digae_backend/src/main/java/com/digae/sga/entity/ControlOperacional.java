package com.digae.sga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "controles_operacionales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControlOperacional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200, unique = true)
    private String descripcion;
    
    @ManyToMany(mappedBy = "controles")
    private List<AspectAmbiental> aspectos;
}
