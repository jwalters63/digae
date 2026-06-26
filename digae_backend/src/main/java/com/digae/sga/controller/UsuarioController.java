package com.digae.sga.controller;

import com.digae.sga.dto.request.UsuarioRequestDTO;
import com.digae.sga.dto.response.UsuarioResponseDTO;
import com.digae.sga.entity.enums.RolUsuario;
import com.digae.sga.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Usuarios del sistema DIGAE.
 *
 * Endpoints:
 *   GET    /api/v1/usuarios            → Lista todos los usuarios (200)
 *   GET    /api/v1/usuarios/{id}       → Obtiene un usuario por ID (200 / 404)
 *   POST   /api/v1/usuarios            → Crea un nuevo usuario (201 / 400 / 409)
 *   PUT    /api/v1/usuarios/{id}       → Actualiza un usuario (200 / 400 / 404 / 409)
 *   DELETE /api/v1/usuarios/{id}       → Elimina un usuario (204 / 404)
 *   GET    /api/v1/usuarios/rol/{rol}  → Lista usuarios por rol (200)
 */
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todos los usuarios del sistema.
     * GET /api/v1/usuarios → 200 OK
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Obtiene un usuario específico por su ID.
     * GET /api/v1/usuarios/{id} → 200 OK / 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Crea un nuevo usuario.
     * POST /api/v1/usuarios → 201 Created / 400 Bad Request / 409 Conflict
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(
            @Valid @RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO nuevoUsuario = usuarioService.crearUsuario(requestDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    /**
     * Actualiza un usuario existente.
     * PUT /api/v1/usuarios/{id} → 200 OK / 400 Bad Request / 404 Not Found / 409 Conflict
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(id, requestDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    /**
     * Elimina un usuario por su ID.
     * DELETE /api/v1/usuarios/{id} → 204 No Content / 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
