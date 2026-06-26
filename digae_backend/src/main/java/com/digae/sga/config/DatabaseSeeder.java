package com.digae.sga.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    private final JdbcTemplate jdbcTemplate;

    @Bean
    @Transactional
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Verificando el estado de la base de datos para seeder nativo...");

            jdbcTemplate.execute("DELETE FROM usuarios WHERE id NOT IN (SELECT id FROM administradores) AND id NOT IN (SELECT id FROM usuarios_operativos)");

            Integer countFacultades = jdbcTemplate.queryForObject("SELECT count(*) FROM facultades WHERE id = 1", Integer.class);
            if (countFacultades != null && countFacultades == 0) {
                log.info("Insertando Facultad Base con ID 1...");
                jdbcTemplate.update("INSERT INTO facultades (id, nombre, siglas) VALUES (1, 'Facultad de Ingeniería', 'FI') ON CONFLICT (id) DO NOTHING");
            }

            Integer countUsuarios = jdbcTemplate.queryForObject("SELECT count(*) FROM usuarios WHERE id = 1", Integer.class);
            String encodedPassword = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin");
            
            if (countUsuarios != null && countUsuarios == 0) {
                log.info("Insertando Administrador Root con ID 1...");
                jdbcTemplate.update("INSERT INTO usuarios (id, nombre, apellido, email, password, activo, creado_en, rol) VALUES (1, 'Admin', 'Root', 'admin', ?, true, NOW(), 'ADMINISTRADOR') ON CONFLICT (id) DO NOTHING", encodedPassword);
                jdbcTemplate.update("INSERT INTO administradores (id) VALUES (1) ON CONFLICT (id) DO NOTHING");
            } else {
                log.info("Actualizando credenciales del Administrador Root (ID 1)...");
                jdbcTemplate.update("UPDATE usuarios SET email = 'admin', password = ? WHERE id = 1", encodedPassword);
                Integer countAdmin = jdbcTemplate.queryForObject("SELECT count(*) FROM administradores WHERE id = 1", Integer.class);
                if (countAdmin != null && countAdmin == 0) {
                    jdbcTemplate.update("INSERT INTO administradores (id) VALUES (1) ON CONFLICT (id) DO NOTHING");
                }
            }

            try {
                jdbcTemplate.execute("SELECT setval(pg_get_serial_sequence('facultades', 'id'), (SELECT MAX(id) FROM facultades))");
                jdbcTemplate.execute("SELECT setval(pg_get_serial_sequence('usuarios', 'id'), (SELECT MAX(id) FROM usuarios))");
            } catch (Exception e) {
                log.warn("No se pudieron ajustar las secuencias, es normal si es H2 o si no hay registros previos.");
            }

            log.info("¡Base de datos nativa en orden y lista para su uso!");
        };
    }
}
