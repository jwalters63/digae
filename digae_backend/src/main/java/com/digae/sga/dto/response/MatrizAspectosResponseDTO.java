package com.digae.sga.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatrizAspectosResponseDTO {

    private Long id;
    private String nombre;
    private LocalDate fechaEvaluacion;
    private Long facultadId;
    private String facultadNombre;
    private Long creadoPorId;
    private String creadoPorNombreCompleto;
    private String estado;
    private List<AspectAmbientalResponseDTO> aspectos;
}
