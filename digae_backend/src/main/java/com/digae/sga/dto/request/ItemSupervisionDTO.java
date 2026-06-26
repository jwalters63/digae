package com.digae.sga.dto.request;

import com.digae.sga.entity.enums.ResultadoSupervision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemSupervisionDTO {

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100, message = "La categoría no debe exceder 100 caracteres")
    private String categoria;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 250, message = "La descripción no debe exceder 250 caracteres")
    private String descripcion;

    @NotNull(message = "El resultado es obligatorio")
    private ResultadoSupervision resultado;
}
