package com.grupo7poo2.digae.modelos

import java.util.Date

// --- PATRÓN ESTRATEGIA (Cálculo de Criticidad) ---
interface CalculoCriticidadStrategy {
    fun calcular(gravedad: Int, severidad: Int, probabilidad: Int): Double
}

class MotorCalculoMultiplicativo : CalculoCriticidadStrategy {
    override fun calcular(gravedad: Int, severidad: Int, probabilidad: Int): Double {
        return (gravedad * severidad * probabilidad).toDouble()
    }
}

// --- CLASES DEL MÓDULO 1: MATRICES Y ASPECTOS ---
class MatrizAspectos(
    val id: String,
    val area: String,
    val actividad: String,
    val fechaRegistro: Date
) {
    private val aspectos = mutableListOf<AspectAmbiental>()

    fun agregarAspecto(aspecto: AspectAmbiental) {
        aspectos.add(aspecto)
    }

    fun calcularCriticidadGlobal(): Double {
        return aspectos.sumOf { it.obtenerNivelCriticidad() }
    }
    
    fun sugerirControlGlobal(): List<ControlOperacional> {
        return aspectos.flatMap { it.controlesAsignados }
    }

    fun guardar() {
        // Simulación de persistencia
    }
}

class AspectAmbiental(
    val id: String,
    val descripcion: String,
    var gravedad: Int,
    var severidad: Int,
    var probabilidad: Int,
    private val calculador: CalculoCriticidadStrategy = MotorCalculoMultiplicativo()
) {
    private var criticidad: Double = 0.0
    val controlesAsignados = mutableListOf<ControlOperacional>()

    fun evaluarImpacto() {
        criticidad = calculador.calcular(gravedad, severidad, probabilidad)
    }

    fun obtenerNivelCriticidad(): Double = criticidad

    fun asignarControl(control: ControlOperacional) {
        if (control.validarAplicabilidad()) {
            controlesAsignados.add(control)
        }
    }
}

class ControlOperacional(
    val id: String,
    val descripcion: String,
    val nivelCriticidadRequerido: Double,
    val tipoControl: String
) {
    fun asignarControl() {
        // Lógica de asignación
    }

    fun validarAplicabilidad(): Boolean {
        return true // Simulación de validación
    }
}
