package com.digae.sga.service;

import com.digae.sga.dto.request.MatrizAspectosRequestDTO;
import com.digae.sga.dto.response.MatrizAspectosResponseDTO;

import java.util.List;

public interface MatrizAspectosService {
    MatrizAspectosResponseDTO create(MatrizAspectosRequestDTO dto);
    List<MatrizAspectosResponseDTO> findAll();
    MatrizAspectosResponseDTO findById(Long id);
    MatrizAspectosResponseDTO update(Long id, MatrizAspectosRequestDTO dto);
    void delete(Long id);
}
