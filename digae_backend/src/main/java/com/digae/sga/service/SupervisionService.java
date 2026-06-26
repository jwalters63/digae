package com.digae.sga.service;

import com.digae.sga.dto.request.SupervisionRequestDTO;
import com.digae.sga.dto.response.SupervisionResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio de Supervisión/Auditoría.
 */
public interface SupervisionService {

    SupervisionResponseDTO crearSupervision(SupervisionRequestDTO requestDTO);

    SupervisionResponseDTO obtenerSupervisionPorId(Long id);

    List<SupervisionResponseDTO> obtenerTodasLasSupervisiones();

    List<SupervisionResponseDTO> obtenerSupervisionesPorInspector(Long inspectorId);

    SupervisionResponseDTO actualizarSupervision(Long id, SupervisionRequestDTO requestDTO);

    void eliminarSupervision(Long id);
}
