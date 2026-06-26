package com.digae.sga.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AspectAmbientalRequestDTO {

    @NotBlank(message = "La descripción del aspecto es obligatoria")
    @Size(max = 200, message = "La descripción no debe exceder 200 caracteres")
    private String descripcion;

    @NotNull(message = "La gravedad es obligatoria")
    @Min(value = 1, message = "La gravedad mínima es 1")
    @Max(value = 5, message = "La gravedad máxima es 5")
    private Integer gravedad;

    @NotNull(message = "La severidad es obligatoria")
    @Min(value = 1, message = "La severidad mínima es 1")
    @Max(value = 5, message = "La severidad máxima es 5")
    private Integer severidad;

    @NotNull(message = "La probabilidad es obligatoria")
    @Min(value = 1, message = "La probabilidad mínima es 1")
    @Max(value = 5, message = "La probabilidad máxima es 5")
    private Integer probabilidad;

    @Valid
    private List<ControlOperacionalDTO> controles = new ArrayList<>();
}
