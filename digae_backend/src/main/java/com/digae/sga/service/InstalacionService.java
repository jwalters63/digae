package com.digae.sga.service;

import com.digae.sga.entity.Instalacion;
import com.digae.sga.repository.InstalacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstalacionService {

    private final InstalacionRepository instalacionRepository;

    public InstalacionService(InstalacionRepository instalacionRepository) {
        this.instalacionRepository = instalacionRepository;
    }

    public List<Instalacion> getAllInstalaciones() {
        return instalacionRepository.findAll();
    }

    @Transactional
    public Instalacion createInstalacion(Instalacion instalacion) {
        return instalacionRepository.save(instalacion);
    }

    @Transactional
    public boolean deleteInstalacion(Long id) {
        if (instalacionRepository.existsById(id)) {
            instalacionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
