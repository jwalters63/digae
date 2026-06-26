package com.digae.sga.service.impl;

import com.digae.sga.dto.request.SupervisionRequestDTO;
import com.digae.sga.dto.response.SupervisionResponseDTO;
import com.digae.sga.entity.Supervision;
import com.digae.sga.entity.Usuario;
import com.digae.sga.exception.ResourceNotFoundException;
import com.digae.sga.mapper.SupervisionMapper;
import com.digae.sga.repository.SupervisionRepository;
import com.digae.sga.repository.UsuarioRepository;
import com.digae.sga.service.SupervisionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupervisionServiceImpl implements SupervisionService {

    private final SupervisionRepository supervisionRepository;
    private final UsuarioRepository usuarioRepository;

    public SupervisionServiceImpl(SupervisionRepository supervisionRepository,
                                  UsuarioRepository usuarioRepository) {
        this.supervisionRepository = supervisionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public SupervisionResponseDTO crearSupervision(SupervisionRequestDTO requestDTO) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario inspector = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        if (!(inspector instanceof com.digae.sga.entity.Administrador)) {
            throw new IllegalArgumentException("Solo los administradores pueden crear supervisiones");
        }

        Supervision supervision = SupervisionMapper.toEntity(requestDTO, inspector);
        Supervision savedSupervision = supervisionRepository.save(supervision);

        return SupervisionMapper.toResponseDTO(savedSupervision);
    }

    @Override
    @Transactional(readOnly = true)
    public SupervisionResponseDTO obtenerSupervisionPorId(Long id) {
        Supervision supervision = supervisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision", "id", id));

        return SupervisionMapper.toResponseDTO(supervision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupervisionResponseDTO> obtenerTodasLasSupervisiones() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario currentUser = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        if (!(currentUser instanceof com.digae.sga.entity.Administrador)) {

            throw new IllegalArgumentException("Solo los administradores pueden acceder a supervisiones");
        }

        return supervisionRepository.findAll()
                .stream()
                .map(SupervisionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupervisionResponseDTO> obtenerSupervisionesPorInspector(Long inspectorId) {

        if (!usuarioRepository.existsById(inspectorId)) {
            throw new ResourceNotFoundException("Usuario (Inspector)", "id", inspectorId);
        }

        return supervisionRepository.findByInspectorId(inspectorId)
                .stream()
                .map(SupervisionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SupervisionResponseDTO actualizarSupervision(Long id,
                                                         SupervisionRequestDTO requestDTO) {
        Supervision supervision = supervisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supervision", "id", id));

        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario inspector = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        if (!(inspector instanceof com.digae.sga.entity.Administrador)) {
            throw new IllegalArgumentException("Solo los administradores pueden editar supervisiones");
        }

        SupervisionMapper.updateEntityFromDTO(requestDTO, supervision, inspector);
        Supervision updatedSupervision = supervisionRepository.save(supervision);

        return SupervisionMapper.toResponseDTO(updatedSupervision);
    }

    @Override
    public void eliminarSupervision(Long id) {
        if (!supervisionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supervision", "id", id);
        }
        supervisionRepository.deleteById(id);
    }

    private Usuario resolverInspector(Long inspectorId) {
        return usuarioRepository.findById(inspectorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Inspector)", "id", inspectorId));
    }
}
