package com.digae.sga.service.impl;

import com.digae.sga.dto.request.MatrizAspectosRequestDTO;
import com.digae.sga.dto.response.MatrizAspectosResponseDTO;
import com.digae.sga.entity.Administrador;
import com.digae.sga.entity.Facultad;
import com.digae.sga.entity.MatrizAspectos;
import com.digae.sga.exception.ResourceNotFoundException;
import com.digae.sga.mapper.MatrizAspectosMapper;
import com.digae.sga.repository.ControlOperacionalRepository;
import com.digae.sga.repository.FacultadRepository;
import com.digae.sga.repository.MatrizAspectosRepository;
import com.digae.sga.repository.UsuarioRepository;
import com.digae.sga.service.CriticidadCalculator;
import com.digae.sga.service.MatrizAspectosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatrizAspectosServiceImpl implements MatrizAspectosService {

    private final MatrizAspectosRepository matrizRepository;
    private final FacultadRepository facultadRepository;
    private final UsuarioRepository usuarioRepository;
    private final ControlOperacionalRepository controlRepository;
    private final CriticidadCalculator criticidadCalculator;

    @Override
    @Transactional
    public MatrizAspectosResponseDTO create(MatrizAspectosRequestDTO dto) {
        Facultad facultad = facultadRepository.findById(dto.getFacultadId())
                .orElseThrow(() -> new ResourceNotFoundException("Facultad", "id", dto.getFacultadId()));
        
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.digae.sga.entity.Usuario currentUser = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        if (!(currentUser instanceof Administrador)) {
            throw new IllegalArgumentException("Solo los administradores pueden crear matrices");
        }

        Administrador admin = (Administrador) currentUser;

        MatrizAspectos matriz = MatrizAspectosMapper.toEntity(dto, facultad, admin);

        // Procesar criticidad para cada aspecto antes de guardar
        if (matriz.getAspectos() != null) {
            matriz.getAspectos().forEach(aspecto -> {
                criticidadCalculator.procesarAspectoAmbiental(aspecto);
                
                // Si el aspect tiene controles (sugeridos o manuales), asegurar que se guarden
                if (aspecto.getControles() != null) {
                    aspecto.setControles(aspecto.getControles().stream().map(ctrl -> {
                        // Buscar si existe un control con la misma descripción
                        return controlRepository.findByDescripcion(ctrl.getDescripcion())
                                .orElseGet(() -> controlRepository.save(ctrl));
                    }).collect(Collectors.toList()));
                }
            });
        }

        MatrizAspectos saved = matrizRepository.save(matriz);
        return MatrizAspectosMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatrizAspectosResponseDTO> findAll() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.digae.sga.entity.Usuario currentUser = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        List<MatrizAspectos> matrices;
        if (currentUser instanceof com.digae.sga.entity.UsuarioOperativo) {
            com.digae.sga.entity.UsuarioOperativo uo = (com.digae.sga.entity.UsuarioOperativo) currentUser;
            // Solo retornar las de su facultad
            matrices = matrizRepository.findAll().stream()
                    .filter(m -> m.getFacultad().getId().equals(uo.getFacultad().getId()))
                    .collect(Collectors.toList());
        } else {
            // Administrador ve todas
            matrices = matrizRepository.findAll();
        }

        return matrices.stream()
                .map(MatrizAspectosMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MatrizAspectosResponseDTO findById(Long id) {
        MatrizAspectos matriz = matrizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matriz", "id", id));
        return MatrizAspectosMapper.toResponseDTO(matriz);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!matrizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Matriz", "id", id);
        }
        matrizRepository.deleteById(id);
    }
}
