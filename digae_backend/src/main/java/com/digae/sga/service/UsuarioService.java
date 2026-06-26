package com.digae.sga.service;

import com.digae.sga.dto.request.UsuarioRequestDTO;
import com.digae.sga.dto.response.UsuarioResponseDTO;
import com.digae.sga.entity.enums.RolUsuario;

import java.util.List;

/**
 * Interfaz del servicio de Usuario.
 * Define las operaciones de negocio disponibles.
 */
public interface UsuarioService {

    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO);

    UsuarioResponseDTO obtenerUsuarioPorId(Long id);

    List<UsuarioResponseDTO> obtenerTodosLosUsuarios();


    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO requestDTO);

    void eliminarUsuario(Long id);
}
