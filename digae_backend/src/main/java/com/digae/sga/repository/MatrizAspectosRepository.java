package com.digae.sga.repository;

import com.digae.sga.entity.MatrizAspectos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatrizAspectosRepository extends JpaRepository<MatrizAspectos, Long> {
}
