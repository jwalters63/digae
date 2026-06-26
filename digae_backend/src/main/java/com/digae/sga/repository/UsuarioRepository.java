package com.digae.sga.repository;

import com.digae.sga.entity.Usuario;
import com.digae.sga.entity.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario.
 * Provee operaciones CRUD y queries personalizadas.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);


    List<Usuario> findByActivoTrue();
}
