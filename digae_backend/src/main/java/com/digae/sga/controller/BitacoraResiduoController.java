package com.digae.sga.controller;

import com.digae.sga.dto.request.BitacoraResiduoRequestDTO;
import com.digae.sga.dto.response.BitacoraResiduoResponseDTO;
import com.digae.sga.entity.enums.ClasificacionResiduo;
import com.digae.sga.service.BitacoraResiduoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la Bitácora de Residuos del sistema DIGAE.
 *
 * Endpoints:
 *   GET    /api/v1/bitacora-residuos                         → Lista todos (200)
 *   GET    /api/v1/bitacora-residuos/{id}                    → Obtiene por ID (200 / 404)
 *   POST   /api/v1/bitacora-residuos                         → Crea registro (201 / 400)
 *   PUT    /api/v1/bitacora-residuos/{id}                    → Actualiza (200 / 400 / 404)
 *   DELETE /api/v1/bitacora-residuos/{id}                    → Elimina (204 / 404)
 *   GET    /api/v1/bitacora-residuos/clasificacion/{tipo}    → Filtra por tipo (200)
 */
@RestController
@RequestMapping("/api/v1/bitacora-residuos")
public class BitacoraResiduoController {

    private final BitacoraResiduoService bitacoraService;

    public BitacoraResiduoController(BitacoraResiduoService bitacoraService) {
        this.bitacoraService = bitacoraService;
    }

    @GetMapping
    public ResponseEntity<List<BitacoraResiduoResponseDTO>> obtenerTodos() {
        List<BitacoraResiduoResponseDTO> registros = bitacoraService.obtenerTodosLosRegistros();
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BitacoraResiduoResponseDTO> obtenerPorId(@PathVariable Long id) {
        BitacoraResiduoResponseDTO registro = bitacoraService.obtenerRegistroPorId(id);
        return ResponseEntity.ok(registro);
    }

    @PostMapping
    public ResponseEntity<BitacoraResiduoResponseDTO> crear(
            @Valid @RequestBody BitacoraResiduoRequestDTO requestDTO) {
        BitacoraResiduoResponseDTO nuevoRegistro = bitacoraService.crearRegistro(requestDTO);
        return new ResponseEntity<>(nuevoRegistro, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BitacoraResiduoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody BitacoraResiduoRequestDTO requestDTO) {
        BitacoraResiduoResponseDTO registroActualizado =
                bitacoraService.actualizarRegistro(id, requestDTO);
        return ResponseEntity.ok(registroActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        bitacoraService.eliminarRegistro(id);
        return ResponseEntity.noContent().build();
    }

}
