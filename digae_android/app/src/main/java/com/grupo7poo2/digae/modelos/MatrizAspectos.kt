package com.grupo7poo2.digae.modelos

import java.util.Date

enum class NivelCriticidad(val label: String, val rangoMin: Double, val rangoMax: Double) {
    BAJA("Baja", 0.0, 19.9),
    MEDIA("Media", 20.0, 59.9),
    ALTA("Alta", 60.0, 119.9),
    CRITICA("Crítica", 120.0, Double.MAX_VALUE);

    companion object {
        fun desde(valor: Double): NivelCriticidad = when {
            valor < 20.0  -> BAJA
            valor < 60.0  -> MEDIA
            valor < 120.0 -> ALTA
            else          -> CRITICA
        }
    }
}

enum class TipoAspecto(val label: String) {
    AIRE("Aire"),
    AGUA("Agua"),
    SUELO("Suelo"),
    RESIDUOS("Residuos"),
    ENERGIA("Energía"),
    BIODIVERSIDAD("Biodiversidad")
}

enum class EstadoMatriz(val label: String) {
    BORRADOR("Borrador"),
    EN_REVISION("En revisión"),
    APROBADA("Aprobada")
}

interface CalculoCriticidadStrategy {
    fun calcular(gravedad: Int, severidad: Int, probabilidad: Int): Double
}

class MotorCalculoMultiplicativo : CalculoCriticidadStrategy {
    override fun calcular(gravedad: Int, severidad: Int, probabilidad: Int): Double =
        (gravedad * severidad * probabilidad).toDouble()
}

class MotorCalculoPonderado : CalculoCriticidadStrategy {
    override fun calcular(gravedad: Int, severidad: Int, probabilidad: Int): Double =
        ((gravedad * 0.5) + (severidad * 0.3) + (probabilidad * 0.2)) * 10
}

class MatrizAspectos(
    val id: String,
    val instalacionId: String,
    val actividad: String,
    val fechaRegistro: Date,
    var estado: EstadoMatriz = EstadoMatriz.BORRADOR
) {
    private val _aspectos = mutableListOf<AspectAmbiental>()

    val aspectos: List<AspectAmbiental> get() = _aspectos.toList()

    fun agregarAspecto(aspecto: AspectAmbiental) {
        aspecto.evaluarImpacto()
        _aspectos.add(aspecto)
    }

    fun calcularCriticidadGlobal(): Double =
        _aspectos.sumOf { it.obtenerNivelCriticidad() }

    fun nivelGlobal(): NivelCriticidad =
        NivelCriticidad.desde(calcularCriticidadGlobal())

    fun aspectosCriticos(): List<AspectAmbiental> =
        _aspectos.filter { it.nivelCriticidad() == NivelCriticidad.CRITICA || it.nivelCriticidad() == NivelCriticidad.ALTA }

    fun sugerirControlGlobal(): List<ControlOperacional> =
        _aspectos.flatMap { it.controlesAsignados }

    fun guardar() {  }
}

class AspectAmbiental(
    val id: String,
    val descripcion: String,
    val descripcionImpacto: String,
    val tipoAspecto: TipoAspecto,
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

    fun nivelCriticidad(): NivelCriticidad = NivelCriticidad.desde(criticidad)

    fun asignarControl(control: ControlOperacional) {
        if (control.validarAplicabilidad()) controlesAsignados.add(control)
    }
}

class ControlOperacional(
    val id: String,
    val descripcion: String,
    val nivelCriticidadRequerido: Double,
    val tipoControl: String
) {
    fun asignarControl() {  }
    fun validarAplicabilidad(): Boolean = true
}
