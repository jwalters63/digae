package com.digae.sga.mapper;

import com.digae.sga.dto.request.AspectAmbientalRequestDTO;
import com.digae.sga.dto.request.ControlOperacionalDTO;
import com.digae.sga.dto.request.MatrizAspectosRequestDTO;
import com.digae.sga.dto.response.AspectAmbientalResponseDTO;
import com.digae.sga.dto.response.MatrizAspectosResponseDTO;
import com.digae.sga.entity.Administrador;
import com.digae.sga.entity.AspectAmbiental;
import com.digae.sga.entity.ControlOperacional;
import com.digae.sga.entity.Facultad;
import com.digae.sga.entity.MatrizAspectos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public final class MatrizAspectosMapper {

    private MatrizAspectosMapper() {}

    public static MatrizAspectos toEntity(MatrizAspectosRequestDTO dto, Facultad facultad, Administrador creadoPor) {
        MatrizAspectos matriz = MatrizAspectos.builder()
                .nombre(dto.getNombre())
                .fechaEvaluacion(dto.getFechaEvaluacion() != null ? dto.getFechaEvaluacion() : LocalDate.now())
                .facultad(facultad)
                .creadoPor(creadoPor)
                .estado(dto.getEstado() != null ? dto.getEstado() : "BORRADOR")
                .aspectos(new ArrayList<>())
                .build();

        if (dto.getAspectos() != null) {
            matriz.getAspectos().addAll(
                dto.getAspectos().stream().map(aspDto -> {
                    AspectAmbiental aspecto = AspectAmbiental.builder()
                            .descripcion(aspDto.getDescripcion())
                            .gravedad(aspDto.getGravedad())
                            .severidad(aspDto.getSeveridad())
                            .probabilidad(aspDto.getProbabilidad())
                            .matriz(matriz)
                            .controles(new ArrayList<>())
                            .build();

                    if (aspDto.getControles() != null) {
                        aspecto.getControles().addAll(
                            aspDto.getControles().stream().map(ctrlDto -> 
                                ControlOperacional.builder()
                                        .descripcion(ctrlDto.getDescripcion())
                                        .build()
                            ).collect(Collectors.toList())
                        );
                    }
                    return aspecto;
                }).collect(Collectors.toList())
            );
        }
        return matriz;
    }

    public static MatrizAspectosResponseDTO toResponseDTO(MatrizAspectos entity) {
        return MatrizAspectosResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .fechaEvaluacion(entity.getFechaEvaluacion())
                .facultadId(entity.getFacultad().getId())
                .facultadNombre(entity.getFacultad().getNombre())
                .creadoPorId(entity.getCreadoPor().getId())
                .creadoPorNombreCompleto(entity.getCreadoPor().getNombre() + " " + entity.getCreadoPor().getApellido())
                .estado(entity.getEstado())
                .aspectos(entity.getAspectos() == null ? new ArrayList<>() : entity.getAspectos().stream().map(asp -> 
                    AspectAmbientalResponseDTO.builder()
                            .id(asp.getId())
                            .descripcion(asp.getDescripcion())
                            .gravedad(asp.getGravedad())
                            .severidad(asp.getSeveridad())
                            .probabilidad(asp.getProbabilidad())
                            .nivelCriticidad(asp.getNivelCriticidad())
                            .controles(asp.getControles() == null ? new ArrayList<>() : asp.getControles().stream().map(ctrl -> 
                                ControlOperacionalDTO.builder()
                                        .id(ctrl.getId())
                                        .descripcion(ctrl.getDescripcion())
                                        .build()
                            ).collect(Collectors.toList()))
                            .build()
                ).collect(Collectors.toList()))
                .build();
    }
}
