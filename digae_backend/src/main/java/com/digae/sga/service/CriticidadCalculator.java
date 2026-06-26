package com.digae.sga.service;

import com.digae.sga.entity.AspectAmbiental;
import com.digae.sga.entity.ControlOperacional;
import com.digae.sga.entity.enums.NivelCriticidad;

import java.util.List;

public interface CriticidadCalculator {
    
    /**
     * Calcula el nivel de criticidad en base a la gravedad, severidad y probabilidad.
     * @param gravedad valor del 1 al 5
     * @param severidad valor del 1 al 5
     * @param probabilidad valor del 1 al 5
     * @return NivelCriticidad calculado
     */
    NivelCriticidad calcularCriticidad(int gravedad, int severidad, int probabilidad);

    /**
     * Sugiere una lista de controles operacionales según el nivel de criticidad.
     * @param nivel Nivel de criticidad
     * @return Lista de controles operacionales sugeridos
     */
    List<ControlOperacional> sugerirControles(NivelCriticidad nivel);
    
    /**
     * Procesa un Aspecto Ambiental, calculando su criticidad y asignándole los controles sugeridos.
     * @param aspecto Aspecto ambiental a procesar
     */
    void procesarAspectoAmbiental(AspectAmbiental aspecto);
}
