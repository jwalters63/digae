package com.digae.sga.repository;

import com.digae.sga.entity.ControlOperacional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ControlOperacionalRepository extends JpaRepository<ControlOperacional, Long> {
    Optional<ControlOperacional> findByDescripcion(String descripcion);
}
