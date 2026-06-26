package com.digae.sga.dto.response;

import com.digae.sga.entity.enums.ClasificacionResiduo;
import com.digae.sga.dto.request.ResiduoDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de response para BitacoraResiduo.
 * Incluye el nombre del usuario que registró la bitácora.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BitacoraResiduoResponseDTO {

    private Long id;
    private LocalDate fechaRegistro;
    private java.util.List<ResiduoDTO> residuos;
    private String areaGeneradora;
    private String empresaRecolectora;
    private String firmaBase64;
    private String observaciones;
    private Long usuarioId;
    private String usuarioNombreCompleto;
    private LocalDateTime creadoEn;
}
