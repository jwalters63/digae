package com.digae.sga.mapper;

import com.digae.sga.dto.request.UsuarioRequestDTO;
import com.digae.sga.dto.response.UsuarioResponseDTO;
import com.digae.sga.entity.Usuario;

import com.digae.sga.entity.Administrador;
import com.digae.sga.entity.UsuarioOperativo;
import com.digae.sga.entity.enums.RolUsuario;

/**
 * Mapper para conversiones entre Usuario Entity y sus DTOs.
 * Usa métodos estáticos para evitar instanciación innecesaria.
 */
public final class UsuarioMapper {

    private UsuarioMapper() {
        // Utility class - no instanciar
    }

    /**
     * Convierte un UsuarioRequestDTO a una entidad Usuario.
     * No copia el ID (se genera automáticamente).
     */
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

    /**
     * Convierte una entidad Usuario a UsuarioResponseDTO.
     * Excluye la contraseña del response por seguridad.
     */
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

    /**
     * Actualiza los campos de una entidad Usuario existente con los datos del DTO.
     * No modifica el ID, creadoEn ni activo.
     */
    public static void updateEntityFromDTO(UsuarioRequestDTO dto, Usuario entity) {
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
    }
}
