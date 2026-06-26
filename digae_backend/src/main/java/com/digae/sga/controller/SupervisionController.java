package com.digae.sga.controller;

import com.digae.sga.dto.request.SupervisionRequestDTO;
import com.digae.sga.dto.response.SupervisionResponseDTO;
import com.digae.sga.service.SupervisionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la Supervisión/Auditoría del sistema DIGAE.
 *
 * Endpoints:
 *   GET    /api/v1/supervisiones                  → Lista todas (200)
 *   GET    /api/v1/supervisiones/{id}              → Obtiene por ID (200 / 404)
 *   POST   /api/v1/supervisiones                  → Crea inspección (201 / 400)
 *   PUT    /api/v1/supervisiones/{id}              → Actualiza (200 / 400 / 404)
 *   DELETE /api/v1/supervisiones/{id}              → Elimina (204 / 404)
 *   GET    /api/v1/supervisiones/inspector/{id}    → Filtra por inspector (200)
 */
@RestController
@RequestMapping("/api/v1/supervisiones")
public class SupervisionController {

    private final SupervisionService supervisionService;

    public SupervisionController(SupervisionService supervisionService) {
        this.supervisionService = supervisionService;
    }

    @GetMapping
    public ResponseEntity<List<SupervisionResponseDTO>> obtenerTodas() {
        List<SupervisionResponseDTO> supervisiones =
                supervisionService.obtenerTodasLasSupervisiones();
        return ResponseEntity.ok(supervisiones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupervisionResponseDTO> obtenerPorId(@PathVariable Long id) {
        SupervisionResponseDTO supervision = supervisionService.obtenerSupervisionPorId(id);
        return ResponseEntity.ok(supervision);
    }

    @PostMapping
    public ResponseEntity<SupervisionResponseDTO> crear(
            @Valid @RequestBody SupervisionRequestDTO requestDTO) {
        SupervisionResponseDTO nuevaSupervision =
                supervisionService.crearSupervision(requestDTO);
        return new ResponseEntity<>(nuevaSupervision, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupervisionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SupervisionRequestDTO requestDTO) {
        SupervisionResponseDTO supervisionActualizada =
                supervisionService.actualizarSupervision(id, requestDTO);
        return ResponseEntity.ok(supervisionActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        supervisionService.eliminarSupervision(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inspector/{inspectorId}")
    public ResponseEntity<List<SupervisionResponseDTO>> obtenerPorInspector(
            @PathVariable Long inspectorId) {
        List<SupervisionResponseDTO> supervisiones =
                supervisionService.obtenerSupervisionesPorInspector(inspectorId);
        return ResponseEntity.ok(supervisiones);
    }
}
