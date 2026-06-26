package com.digae.sga.mapper;

import com.digae.sga.dto.request.UsuarioRequestDTO;
import com.digae.sga.dto.response.UsuarioResponseDTO;
import com.digae.sga.entity.Usuario;

import com.digae.sga.entity.Administrador;
import com.digae.sga.entity.UsuarioOperativo;
import com.digae.sga.entity.enums.RolUsuario;

public final class UsuarioMapper {

    private UsuarioMapper() {

    }

    public static Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto.getRol() == RolUsuario.ADMINISTRADOR) {
            return Administrador.builder()
                    .nombre(dto.getNombre())
                    .apellido(dto.getApellido())
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .activo(true)
                    .build();
        } else {
            return UsuarioOperativo.builder()
                    .nombre(dto.getNombre())
                    .apellido(dto.getApellido())
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .activo(true)
                    .build();
        }
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario entity) {
        RolUsuario rol = (entity instanceof Administrador) ? RolUsuario.ADMINISTRADOR : RolUsuario.OPERATIVO;
        return UsuarioResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .email(entity.getEmail())
                .rol(rol)
                .activo(entity.getActivo())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }

    public static void updateEntityFromDTO(UsuarioRequestDTO dto, Usuario entity) {
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
    }
}
