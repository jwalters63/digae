package com.digae.sga.controller;

import com.digae.sga.dto.request.LoginRequestDTO;
import com.digae.sga.dto.request.RegisterRequestDTO;
import com.digae.sga.dto.response.AuthResponseDTO;
import com.digae.sga.entity.Administrador;
import com.digae.sga.entity.Facultad;
import com.digae.sga.entity.Usuario;
import com.digae.sga.entity.UsuarioOperativo;
import com.digae.sga.entity.enums.RolUsuario;
import com.digae.sga.repository.FacultadRepository;
import com.digae.sga.repository.UsuarioRepository;
import com.digae.sga.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final FacultadRepository facultadRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email o contraseña incorrecta"));

        String jwtToken = jwtTokenProvider.generateToken(user);
        
        Long facultadId = null;
        if (user instanceof UsuarioOperativo) {
            facultadId = ((UsuarioOperativo) user).getFacultad().getId();
        }

        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(jwtToken)
                .id(user.getId())
                .nombre(user.getNombre() + " " + user.getApellido())
                .email(user.getEmail())
                .rol(user.getRol().name())
                .facultadId(facultadId)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario nuevoUsuario;

        if (request.getRol() == RolUsuario.ADMINISTRADOR) {
            nuevoUsuario = Administrador.builder()
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .activo(true)
                    .build();
        } else {
            if (request.getFacultadId() == null) {
                throw new IllegalArgumentException("La facultad es requerida para un Usuario Operativo");
            }
            Facultad facultad = facultadRepository.findById(request.getFacultadId())
                    .orElseThrow(() -> new IllegalArgumentException("Facultad no encontrada"));

            nuevoUsuario = UsuarioOperativo.builder()
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .activo(true)
                    .facultad(facultad)
                    .build();
        }

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .email(nuevoUsuario.getEmail())
                .rol(nuevoUsuario.getRol().name())
                .build());
    }
}
