package com.digae.sga.mapper;

import com.digae.sga.dto.request.BitacoraResiduoRequestDTO;
import com.digae.sga.dto.response.BitacoraResiduoResponseDTO;
import com.digae.sga.entity.BitacoraResiduo;
import com.digae.sga.entity.Usuario;

public final class BitacoraResiduoMapper {

    private BitacoraResiduoMapper() {

    }

    public static BitacoraResiduo toEntity(BitacoraResiduoRequestDTO dto, Usuario usuario) {
        BitacoraResiduo bitacora = BitacoraResiduo.builder()
                .fechaRegistro(dto.getFechaRegistro())
                .areaGeneradora(dto.getAreaGeneradora())
                .empresaRecolectora(dto.getEmpresaRecolectora())
                .firmaBase64(dto.getFirmaBase64())
                .observaciones(dto.getObservaciones())
                .usuario(usuario)
                .build();

        if (dto.getResiduos() != null) {
            java.util.List<com.digae.sga.entity.Residuo> residuos = dto.getResiduos().stream().map(rDTO -> 
                com.digae.sga.entity.Residuo.builder()
                    .clasificacion(rDTO.getClasificacion())
                    .pesoKg(rDTO.getPesoKg())
                    .volumenM3(rDTO.getVolumenM3())
                    .bitacora(bitacora)
                    .build()
            ).collect(java.util.stream.Collectors.toList());
            bitacora.setResiduos(residuos);
        }

        return bitacora;
    }

    public static BitacoraResiduoResponseDTO toResponseDTO(BitacoraResiduo entity) {
        BitacoraResiduoResponseDTO dto = BitacoraResiduoResponseDTO.builder()
                .id(entity.getId())
                .fechaRegistro(entity.getFechaRegistro())
                .areaGeneradora(entity.getAreaGeneradora())
                .empresaRecolectora(entity.getEmpresaRecolectora())
                .firmaBase64(entity.getFirmaBase64())
                .observaciones(entity.getObservaciones())
                .usuarioId(entity.getUsuario().getId())
                .usuarioNombreCompleto(
                        entity.getUsuario().getNombre() + " " + entity.getUsuario().getApellido()
                )
                .creadoEn(entity.getCreadoEn())
                .build();

        if (entity.getResiduos() != null) {
            java.util.List<com.digae.sga.dto.request.ResiduoDTO> residuosDTO = entity.getResiduos().stream().map(r ->
                com.digae.sga.dto.request.ResiduoDTO.builder()
                    .clasificacion(r.getClasificacion())
                    .pesoKg(r.getPesoKg())
                    .volumenM3(r.getVolumenM3())
                    .build()
            ).collect(java.util.stream.Collectors.toList());
            dto.setResiduos(residuosDTO);
        }

        return dto;
    }

    public static void updateEntityFromDTO(BitacoraResiduoRequestDTO dto,
                                           BitacoraResiduo entity,
                                           Usuario usuario) {
        entity.setFechaRegistro(dto.getFechaRegistro());
        entity.setAreaGeneradora(dto.getAreaGeneradora());
        entity.setEmpresaRecolectora(dto.getEmpresaRecolectora());
        entity.setFirmaBase64(dto.getFirmaBase64());
        entity.setObservaciones(dto.getObservaciones());
        entity.setUsuario(usuario);

        if (dto.getResiduos() != null) {
            entity.getResiduos().clear();
            entity.getResiduos().addAll(dto.getResiduos().stream().map(rDTO -> 
                com.digae.sga.entity.Residuo.builder()
                    .clasificacion(rDTO.getClasificacion())
                    .pesoKg(rDTO.getPesoKg())
                    .volumenM3(rDTO.getVolumenM3())
                    .bitacora(entity)
                    .build()
            ).collect(java.util.stream.Collectors.toList()));
        }
    }
}
