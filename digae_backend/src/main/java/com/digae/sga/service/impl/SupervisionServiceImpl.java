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

/**
 * Implementación del servicio de Supervisión/Auditoría.
 * Gestiona formularios de inspección de campo.
 */
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
        Usuario inspector = resolverInspector(requestDTO.getInspectorId());

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
        return supervisionRepository.findAll()
                .stream()
                .map(SupervisionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupervisionResponseDTO> obtenerSupervisionesPorInspector(Long inspectorId) {
        // Verificar que el inspector existe
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

        Usuario inspector = resolverInspector(requestDTO.getInspectorId());

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

    /**
     * Resuelve el inspector (Usuario) a partir de su ID.
     */
    private Usuario resolverInspector(Long inspectorId) {
        return usuarioRepository.findById(inspectorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Inspector)", "id", inspectorId));
    }
}
