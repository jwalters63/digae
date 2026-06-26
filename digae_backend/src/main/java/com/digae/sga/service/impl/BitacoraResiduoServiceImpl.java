package com.digae.sga.service.impl;

import com.digae.sga.dto.request.BitacoraResiduoRequestDTO;
import com.digae.sga.dto.response.BitacoraResiduoResponseDTO;
import com.digae.sga.entity.BitacoraResiduo;
import com.digae.sga.entity.Usuario;
import com.digae.sga.entity.enums.ClasificacionResiduo;
import com.digae.sga.exception.ResourceNotFoundException;
import com.digae.sga.mapper.BitacoraResiduoMapper;
import com.digae.sga.repository.BitacoraResiduoRepository;
import com.digae.sga.repository.UsuarioRepository;
import com.digae.sga.service.BitacoraResiduoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Bitácora de Residuos.
 * Gestiona el registro de salida de residuos sólidos.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BitacoraResiduoServiceImpl implements BitacoraResiduoService {

    private final BitacoraResiduoRepository bitacoraRepository;
    private final UsuarioRepository usuarioRepository;

    public BitacoraResiduoServiceImpl(BitacoraResiduoRepository bitacoraRepository,
                                      UsuarioRepository usuarioRepository) {
        this.bitacoraRepository = bitacoraRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public BitacoraResiduoResponseDTO crearRegistro(BitacoraResiduoRequestDTO requestDTO) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        BitacoraResiduo bitacora = BitacoraResiduoMapper.toEntity(requestDTO, usuario);
        BitacoraResiduo savedBitacora = bitacoraRepository.save(bitacora);

        return BitacoraResiduoMapper.toResponseDTO(savedBitacora);
    }

    @Override
    @Transactional(readOnly = true)
    public BitacoraResiduoResponseDTO obtenerRegistroPorId(Long id) {
        BitacoraResiduo bitacora = bitacoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BitacoraResiduo", "id", id));

        return BitacoraResiduoMapper.toResponseDTO(bitacora);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BitacoraResiduoResponseDTO> obtenerTodosLosRegistros() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario currentUser = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        List<BitacoraResiduo> registros = bitacoraRepository.findAll();
        
        if (currentUser instanceof com.digae.sga.entity.UsuarioOperativo) {
            com.digae.sga.entity.UsuarioOperativo uo = (com.digae.sga.entity.UsuarioOperativo) currentUser;
            registros = registros.stream()
                    .filter(b -> b.getUsuario() instanceof com.digae.sga.entity.UsuarioOperativo 
                            && ((com.digae.sga.entity.UsuarioOperativo)b.getUsuario()).getFacultad().getId().equals(uo.getFacultad().getId()))
                    .collect(Collectors.toList());
        }

        return registros.stream()
                .map(BitacoraResiduoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    @Override
    public BitacoraResiduoResponseDTO actualizarRegistro(Long id,
                                                          BitacoraResiduoRequestDTO requestDTO) {
        BitacoraResiduo bitacora = bitacoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BitacoraResiduo", "id", id));

        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        BitacoraResiduoMapper.updateEntityFromDTO(requestDTO, bitacora, usuario);
        BitacoraResiduo updatedBitacora = bitacoraRepository.save(bitacora);

        return BitacoraResiduoMapper.toResponseDTO(updatedBitacora);
    }

    @Override
    public void eliminarRegistro(Long id) {
        if (!bitacoraRepository.existsById(id)) {
            throw new ResourceNotFoundException("BitacoraResiduo", "id", id);
        }
        bitacoraRepository.deleteById(id);
    }

    /**
     * Resuelve el Usuario a partir de su ID, lanzando excepción si no existe.
     */
    private Usuario resolverUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
    }
}
