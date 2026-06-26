package com.digae.sga.dto.response;

import com.digae.sga.dto.request.ControlOperacionalDTO;
import com.digae.sga.entity.enums.NivelCriticidad;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AspectAmbientalResponseDTO {

    private Long id;
    private String descripcion;
    private Integer gravedad;
    private Integer severidad;
    private Integer probabilidad;
    private NivelCriticidad nivelCriticidad;
    private List<ControlOperacionalDTO> controles;
}
