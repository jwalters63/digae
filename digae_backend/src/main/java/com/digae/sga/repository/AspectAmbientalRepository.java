package com.digae.sga.repository;

import com.digae.sga.entity.AspectAmbiental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AspectAmbientalRepository extends JpaRepository<AspectAmbiental, Long> {
}
