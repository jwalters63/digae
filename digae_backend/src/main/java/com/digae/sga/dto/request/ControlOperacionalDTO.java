package com.digae.sga.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControlOperacionalDTO {

    private Long id;

    @NotBlank(message = "La descripción del control es obligatoria")
    @Size(max = 200, message = "La descripción no debe exceder 200 caracteres")
    private String descripcion;
}
