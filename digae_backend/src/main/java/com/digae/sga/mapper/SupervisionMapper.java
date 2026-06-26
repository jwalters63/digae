package com.digae.sga.mapper;

import com.digae.sga.dto.request.SupervisionRequestDTO;
import com.digae.sga.dto.response.SupervisionResponseDTO;
import com.digae.sga.entity.Supervision;
import com.digae.sga.entity.Usuario;

public final class SupervisionMapper {

    private SupervisionMapper() {

    }

    public static Supervision toEntity(SupervisionRequestDTO dto, Usuario inspector) {
        Supervision supervision = Supervision.builder()
                .fechaInspeccion(dto.getFechaInspeccion())
                .areaInspeccionada(dto.getAreaInspeccionada())
                .calificacionGeneral(dto.getCalificacionGeneral())
                .observaciones(dto.getObservaciones())
                .inspector(inspector)
                .build();

        if (dto.getItems() != null) {
            java.util.List<com.digae.sga.entity.ItemSupervision> items = dto.getItems().stream().map(iDTO -> 
                com.digae.sga.entity.ItemSupervision.builder()
                    .categoria(iDTO.getCategoria())
                    .descripcion(iDTO.getDescripcion())
                    .resultado(iDTO.getResultado())
                    .supervision(supervision)
                    .build()
            ).collect(java.util.stream.Collectors.toList());
            supervision.setItems(items);
        }

        return supervision;
    }

    public static SupervisionResponseDTO toResponseDTO(Supervision entity) {
        SupervisionResponseDTO dto = SupervisionResponseDTO.builder()
                .id(entity.getId())
                .fechaInspeccion(entity.getFechaInspeccion())
                .areaInspeccionada(entity.getAreaInspeccionada())
                .calificacionGeneral(entity.getCalificacionGeneral())
                .observaciones(entity.getObservaciones())
                .inspectorId(entity.getInspector().getId())
                .inspectorNombreCompleto(
                        entity.getInspector().getNombre() + " " + entity.getInspector().getApellido()
                )
                .creadoEn(entity.getCreadoEn())
                .build();

        if (entity.getItems() != null) {
            java.util.List<com.digae.sga.dto.request.ItemSupervisionDTO> itemsDTO = entity.getItems().stream().map(i ->
                com.digae.sga.dto.request.ItemSupervisionDTO.builder()
                    .categoria(i.getCategoria())
                    .descripcion(i.getDescripcion())
                    .resultado(i.getResultado())
                    .build()
            ).collect(java.util.stream.Collectors.toList());
            dto.setItems(itemsDTO);
        }

        return dto;
    }

    public static void updateEntityFromDTO(SupervisionRequestDTO dto, Supervision entity, Usuario inspector) {
        if (dto.getFechaInspeccion() != null) {
            entity.setFechaInspeccion(dto.getFechaInspeccion());
        }
        entity.setAreaInspeccionada(dto.getAreaInspeccionada());
        entity.setCalificacionGeneral(dto.getCalificacionGeneral());
        entity.setObservaciones(dto.getObservaciones());
        entity.setInspector(inspector);

        if (dto.getItems() != null) {
            entity.getItems().clear();
            entity.getItems().addAll(dto.getItems().stream().map(iDTO -> 
                com.digae.sga.entity.ItemSupervision.builder()
                    .categoria(iDTO.getCategoria())
                    .descripcion(iDTO.getDescripcion())
                    .resultado(iDTO.getResultado())
                    .supervision(entity)
                    .build()
            ).collect(java.util.stream.Collectors.toList()));
        }
    }
}
