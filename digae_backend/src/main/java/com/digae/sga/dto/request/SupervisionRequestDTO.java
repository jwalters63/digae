package com.digae.sga.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupervisionRequestDTO {

    private LocalDate fechaInspeccion;

    @NotBlank(message = "El área inspeccionada es obligatoria")
    @Size(max = 200, message = "El área inspeccionada no debe exceder 200 caracteres")
    private String areaInspeccionada;

    @Valid
    private List<ItemSupervisionDTO> items = new ArrayList<>();

    @NotNull(message = "La calificación general es obligatoria")
    @DecimalMin(value = "0.00", message = "La calificación no puede ser negativa")
    @DecimalMax(value = "10.00", message = "La calificación no puede exceder 10.00")
    @Digits(integer = 2, fraction = 2, message = "La calificación debe tener máximo 2 dígitos enteros y 2 decimales")
    private BigDecimal calificacionGeneral;

    @Size(max = 2000, message = "Las observaciones no deben exceder 2000 caracteres")
    private String observaciones;

    @NotNull(message = "El ID del inspector es obligatorio")
    private Long inspectorId;
}
