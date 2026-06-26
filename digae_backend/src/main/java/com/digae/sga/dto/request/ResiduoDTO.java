package com.digae.sga.dto.request;

import com.digae.sga.entity.enums.ClasificacionResiduo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResiduoDTO {

    @NotNull(message = "La clasificación del residuo es obligatoria")
    private ClasificacionResiduo clasificacion;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El peso debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal pesoKg;

    @Digits(integer = 8, fraction = 2, message = "El volumen debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal volumenM3;
}
