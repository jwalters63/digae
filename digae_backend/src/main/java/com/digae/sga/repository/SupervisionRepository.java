package com.digae.sga.repository;

import com.digae.sga.entity.Supervision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad Supervision.
 * Provee queries por inspector y rangos de fecha.
 */
@Repository
public interface SupervisionRepository extends JpaRepository<Supervision, Long> {

    List<Supervision> findByInspectorId(Long inspectorId);

    List<Supervision> findByFechaInspeccionBetween(LocalDate inicio, LocalDate fin);

    List<Supervision> findByAreaInspeccionada(String areaInspeccionada);
}
