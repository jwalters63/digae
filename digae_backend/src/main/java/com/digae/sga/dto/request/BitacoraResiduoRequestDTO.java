package com.digae.sga.dto.request;

import com.digae.sga.entity.enums.ClasificacionResiduo;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * DTO de request para crear o actualizar un registro de BitacoraResiduo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BitacoraResiduoRequestDTO {

    private LocalDate fechaRegistro;

    @NotBlank(message = "El área generadora es obligatoria")
    @Size(max = 200, message = "El área generadora no debe exceder 200 caracteres")
    private String areaGeneradora;

    @NotBlank(message = "La empresa recolectora es obligatoria")
    @Size(max = 200, message = "La empresa recolectora no debe exceder 200 caracteres")
    private String empresaRecolectora;

    private String firmaBase64;

    @Size(max = 2000, message = "Las observaciones no deben exceder 2000 caracteres")
    private String observaciones;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @Valid
    private List<ResiduoDTO> residuos = new ArrayList<>();
}
