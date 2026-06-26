package com.digae.sga.service;

import com.digae.sga.entity.AspectAmbiental;
import com.digae.sga.entity.ControlOperacional;
import com.digae.sga.entity.enums.NivelCriticidad;

import java.util.List;

public interface CriticidadCalculator {

    NivelCriticidad calcularCriticidad(int gravedad, int severidad, int probabilidad);

    List<ControlOperacional> sugerirControles(NivelCriticidad nivel);

    void procesarAspectoAmbiental(AspectAmbiental aspecto);
}
