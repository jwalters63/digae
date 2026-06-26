package com.digae.sga.service.impl;

import com.digae.sga.entity.AspectAmbiental;
import com.digae.sga.entity.ControlOperacional;
import com.digae.sga.entity.enums.NivelCriticidad;
import com.digae.sga.service.CriticidadCalculator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CriticidadCalculatorImpl implements CriticidadCalculator {

    @Override
    public NivelCriticidad calcularCriticidad(int gravedad, int severidad, int probabilidad) {
        int puntaje = gravedad * severidad * probabilidad;
        
        // Asignación de niveles según el puntaje (máximo 125, mínimo 1)
        if (puntaje <= 20) {
            return NivelCriticidad.BAJO;
        } else if (puntaje <= 50) {
            return NivelCriticidad.MEDIO;
        } else if (puntaje <= 90) {
            return NivelCriticidad.ALTO;
        } else {
            return NivelCriticidad.CRITICO;
        }
    }

    @Override
    public List<ControlOperacional> sugerirControles(NivelCriticidad nivel) {
        List<ControlOperacional> sugeridos = new ArrayList<>();
        // Lógica de negocio (simulada) para obtener controles por nivel
        // En una app real, esto podría consultar un repositorio de controles base.
        if (nivel == NivelCriticidad.CRITICO || nivel == NivelCriticidad.ALTO) {
            sugeridos.add(ControlOperacional.builder().descripcion("Plan de contingencia inmediato").build());
            sugeridos.add(ControlOperacional.builder().descripcion("Auditoría externa mensual").build());
        } else {
            sugeridos.add(ControlOperacional.builder().descripcion("Capacitación semestral de personal").build());
        }
        return sugeridos;
    }

    @Override
    public void procesarAspectoAmbiental(AspectAmbiental aspecto) {
        NivelCriticidad nivel = calcularCriticidad(aspecto.getGravedad(), aspecto.getSeveridad(), aspecto.getProbabilidad());
        aspecto.setNivelCriticidad(nivel);
        aspecto.setControles(sugerirControles(nivel));
    }
}
