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
    private final com.digae.sga.repository.InstalacionRepository instalacionRepository;

    @Override
    @Transactional
    public MatrizAspectosResponseDTO create(MatrizAspectosRequestDTO dto) {
        com.digae.sga.entity.Instalacion instalacion = instalacionRepository.findById(dto.getFacultadId()).orElse(null);
        Facultad facultad = null;
        if (instalacion != null) {
            facultad = facultadRepository.findAll().stream()
                    .filter(f -> f.getNombre().equals(instalacion.getNombre()))
                    .findFirst()
                    .orElse(null);
        }
        if (facultad == null) {
            facultad = facultadRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException("Facultad fallback", "id", 1L));
        }

        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.digae.sga.entity.Usuario currentUser = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        if (!(currentUser instanceof Administrador)) {
            throw new IllegalArgumentException("Solo los administradores pueden crear matrices");
        }

        Administrador admin = (Administrador) currentUser;

        MatrizAspectos matriz = MatrizAspectosMapper.toEntity(dto, facultad, admin);

        if (matriz.getAspectos() != null) {
            matriz.getAspectos().forEach(aspecto -> {
                criticidadCalculator.procesarAspectoAmbiental(aspecto);

                if (aspecto.getControles() != null) {
                    aspecto.setControles(aspecto.getControles().stream().map(ctrl -> {

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

            matrices = matrizRepository.findAll().stream()
                    .filter(m -> m.getFacultad().getId().equals(uo.getFacultad().getId()))
                    .collect(Collectors.toList());
        } else {

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
    public MatrizAspectosResponseDTO update(Long id, MatrizAspectosRequestDTO dto) {
        MatrizAspectos matriz = matrizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matriz", "id", id));

        matriz.setNombre(dto.getNombre());
        if (dto.getFechaEvaluacion() != null) {
            matriz.setFechaEvaluacion(dto.getFechaEvaluacion());
        }
        if (dto.getEstado() != null) {
            matriz.setEstado(dto.getEstado());
        }
        
        // Adapter para update
        com.digae.sga.entity.Instalacion instalacionUpdate = instalacionRepository.findById(dto.getFacultadId()).orElse(null);
        Facultad facultadUpdate = null;
        if (instalacionUpdate != null) {
            facultadUpdate = facultadRepository.findAll().stream()
                    .filter(f -> f.getNombre().equals(instalacionUpdate.getNombre()))
                    .findFirst()
                    .orElse(null);
        }
        if (facultadUpdate == null) {
            facultadUpdate = facultadRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException("Facultad fallback", "id", 1L));
        }
        if (!matriz.getFacultad().getId().equals(facultadUpdate.getId())) {
            matriz.setFacultad(facultadUpdate);
        }

        MatrizAspectos saved = matrizRepository.save(matriz);
        return MatrizAspectosMapper.toResponseDTO(saved);
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
