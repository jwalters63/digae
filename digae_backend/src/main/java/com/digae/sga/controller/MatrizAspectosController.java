package com.digae.sga.controller;

import com.digae.sga.dto.request.MatrizAspectosRequestDTO;
import com.digae.sga.dto.response.MatrizAspectosResponseDTO;
import com.digae.sga.service.MatrizAspectosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matrices")
@RequiredArgsConstructor
public class MatrizAspectosController {

    private final MatrizAspectosService matrizService;

    @PostMapping
    public ResponseEntity<MatrizAspectosResponseDTO> create(@Valid @RequestBody MatrizAspectosRequestDTO requestDTO) {
        MatrizAspectosResponseDTO response = matrizService.create(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MatrizAspectosResponseDTO>> findAll() {
        return ResponseEntity.ok(matrizService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatrizAspectosResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(matrizService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatrizAspectosResponseDTO> update(@PathVariable Long id, @Valid @RequestBody MatrizAspectosRequestDTO requestDTO) {
        MatrizAspectosResponseDTO response = matrizService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matrizService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
