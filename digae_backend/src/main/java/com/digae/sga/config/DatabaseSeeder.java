package com.digae.sga.config;

import com.digae.sga.entity.Administrador;
import com.digae.sga.entity.Facultad;
import com.digae.sga.entity.Usuario;
import com.digae.sga.repository.FacultadRepository;
import com.digae.sga.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    private final FacultadRepository facultadRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Verificando el estado de la base de datos para seeder nativo (vía JPA)...");

            // 1. Asegurar Facultad Base
            Optional<Facultad> facultadOpt = facultadRepository.findById(1L);
            if (facultadOpt.isEmpty()) {
                log.info("Insertando Facultad Base con ID 1...");
                Facultad facultad = Facultad.builder()
                        .nombre("Facultad de Ingeniería")
                        .siglas("FI")
                        .build();
                // Forzar ID 1 si es posible, aunque JPA generará uno si usa IDENTITY.
                facultad.setId(1L);
                facultadRepository.save(facultad);
            }

            // 2. Asegurar Administrador Root (admin/admin)
            Optional<Usuario> adminOpt = usuarioRepository.findByEmail("admin");
            if (adminOpt.isEmpty()) {
                log.info("Insertando Administrador Root (admin/admin)...");
                Administrador admin = Administrador.builder()
                        .nombre("Admin")
                        .apellido("Root")
                        .email("admin")
                        .password(passwordEncoder.encode("admin"))
                        .activo(true)
                        .build();
                usuarioRepository.save(admin);
            } else {
                log.info("Actualizando credenciales del Administrador Root (admin/admin)...");
                Usuario user = adminOpt.get();
                user.setPassword(passwordEncoder.encode("admin"));
                usuarioRepository.save(user);
            }

            log.info("¡Base de datos nativa en orden y lista para su uso!");
        };
    }
}
