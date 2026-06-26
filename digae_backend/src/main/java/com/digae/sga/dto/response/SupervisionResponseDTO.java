package com.digae.sga.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupervisionResponseDTO {

    private Long id;
    private LocalDate fechaInspeccion;
    private String areaInspeccionada;
    private java.util.List<com.digae.sga.dto.request.ItemSupervisionDTO> items;
    private BigDecimal calificacionGeneral;
    private String observaciones;
    private Long inspectorId;
    private String inspectorNombreCompleto;
    private LocalDateTime creadoEn;
}
