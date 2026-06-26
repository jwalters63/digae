package com.digae.sga.service;

import com.digae.sga.dto.request.BitacoraResiduoRequestDTO;
import com.digae.sga.dto.response.BitacoraResiduoResponseDTO;
import com.digae.sga.entity.enums.ClasificacionResiduo;

import java.util.List;

/**
 * Interfaz del servicio de Bitácora de Residuos.
 */
public interface BitacoraResiduoService {

    BitacoraResiduoResponseDTO crearRegistro(BitacoraResiduoRequestDTO requestDTO);

    BitacoraResiduoResponseDTO obtenerRegistroPorId(Long id);

    List<BitacoraResiduoResponseDTO> obtenerTodosLosRegistros();

    BitacoraResiduoResponseDTO actualizarRegistro(Long id, BitacoraResiduoRequestDTO requestDTO);

    void eliminarRegistro(Long id);
}
