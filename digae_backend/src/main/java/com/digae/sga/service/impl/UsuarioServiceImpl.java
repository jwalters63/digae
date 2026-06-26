package com.digae.sga.service.impl;

import com.digae.sga.dto.request.UsuarioRequestDTO;
import com.digae.sga.dto.response.UsuarioResponseDTO;
import com.digae.sga.entity.Usuario;
import com.digae.sga.entity.enums.RolUsuario;
import com.digae.sga.exception.BusinessException;
import com.digae.sga.exception.ResourceNotFoundException;
import com.digae.sga.mapper.UsuarioMapper;
import com.digae.sga.repository.UsuarioRepository;
import com.digae.sga.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Usuario.
 * Contiene la lógica de negocio para operaciones CRUD y validaciones.
 */
@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO) {
        // Validar que el email no esté duplicado
        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BusinessException(
                    "Ya existe un usuario registrado con el email: " + requestDTO.getEmail()
            );
        }

        Usuario usuario = UsuarioMapper.toEntity(requestDTO);
        Usuario savedUsuario = usuarioRepository.save(usuario);

        return UsuarioMapper.toResponseDTO(savedUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        return UsuarioMapper.toResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    @Override
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Validar que el nuevo email no esté en uso por otro usuario
        if (!usuario.getEmail().equals(requestDTO.getEmail())
                && usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BusinessException(
                    "El email '" + requestDTO.getEmail() + "' ya está en uso por otro usuario"
            );
        }

        UsuarioMapper.updateEntityFromDTO(requestDTO, usuario);
        Usuario updatedUsuario = usuarioRepository.save(usuario);

        return UsuarioMapper.toResponseDTO(updatedUsuario);
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
        usuarioRepository.deleteById(id);
    }
}
