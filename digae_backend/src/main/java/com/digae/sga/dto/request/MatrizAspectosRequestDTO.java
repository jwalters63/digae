package com.digae.sga.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatrizAspectosRequestDTO {

    @NotBlank(message = "El nombre de la matriz es obligatorio")
    @Size(max = 150, message = "El nombre no debe exceder 150 caracteres")
    private String nombre;

    private LocalDate fechaEvaluacion;

    @NotNull(message = "El ID de la facultad es obligatorio")
    private Long facultadId;

    @NotNull(message = "El ID del administrador (creadoPor) es obligatorio")
    private Long creadoPorId;

    @Valid
    private List<AspectAmbientalRequestDTO> aspectos = new ArrayList<>();
}
