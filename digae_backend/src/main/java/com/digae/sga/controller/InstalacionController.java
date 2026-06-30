package com.digae.sga.controller;

import com.digae.sga.entity.Instalacion;
import com.digae.sga.service.InstalacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instalaciones")
@CrossOrigin(origins = "*")
public class InstalacionController {

    private final InstalacionService instalacionService;

    public InstalacionController(InstalacionService instalacionService) {
        this.instalacionService = instalacionService;
    }

    @GetMapping
    public ResponseEntity<List<Instalacion>> getAllInstalaciones() {
        return ResponseEntity.ok(instalacionService.getAllInstalaciones());
    }

    @PostMapping
    public ResponseEntity<Instalacion> createInstalacion(@RequestBody Instalacion instalacion) {
        Instalacion saved = instalacionService.createInstalacion(instalacion);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstalacion(@PathVariable Long id) {
        boolean deleted = instalacionService.deleteInstalacion(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
