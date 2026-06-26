package com.digae.sga.repository;

import com.digae.sga.entity.BitacoraResiduo;
import com.digae.sga.entity.enums.ClasificacionResiduo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BitacoraResiduoRepository extends JpaRepository<BitacoraResiduo, Long> {

    List<BitacoraResiduo> findByAreaGeneradora(String areaGeneradora);

    List<BitacoraResiduo> findByFechaRegistroBetween(LocalDate inicio, LocalDate fin);

    List<BitacoraResiduo> findByUsuarioId(Long usuarioId);
}
