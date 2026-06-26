package com.grupo7poo2.digae.modelos

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class EstadoSupervision(val label: String) {
    PROGRAMADA("Programada"),
    EN_PROGRESO("En progreso"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada")
}

enum class TipoSupervision(val label: String) {
    INFRAESTRUCTURA("Infraestructura"),
    ENERGIA("Energía"),
    AGUA("Agua y Saneamiento"),
    RESIDUOS("Manejo de Residuos"),
    SEGURIDAD("Seguridad Ambiental")
}

enum class CategoriaItem(val label: String) {
    INSTALACIONES("Instalaciones físicas"),
    EQUIPOS("Equipos y maquinaria"),
    DOCUMENTACION("Documentación"),
    PERSONAL("Personal y capacitación"),
    PROCEDIMIENTOS("Procedimientos operativos")
}

enum class ResultadoSupervision(val label: String) {
    CUMPLE("Cumple"),
    NO_CUMPLE("No cumple"),
    NO_APLICA("No aplica")
}

class Supervision(
    val id: String,
    val instalacionId: String,
    val tipo: TipoSupervision,
    val fecha: Date,
    val supervisor: String,           
    var estado: EstadoSupervision = EstadoSupervision.PROGRAMADA
) {
    private val _items = mutableListOf<ItemSupervision>()

    val items: List<ItemSupervision> get() = _items.toList()

    fun agregarItem(item: ItemSupervision) {
        _items.add(item)
    }

    fun totalItems(): Int = _items.size

    fun itemsCompletados(): Int =
        _items.count { it.resultado != ResultadoSupervision.NO_APLICA }

    fun itemsConResultado(): Int =
        _items.count { it.resultado == ResultadoSupervision.CUMPLE || it.resultado == ResultadoSupervision.NO_CUMPLE }

    fun porcentajeCumplimiento(): Double {
        val evaluados = _items.count { it.resultado != ResultadoSupervision.NO_APLICA }
        if (evaluados == 0) return 0.0
        val cumplen = _items.count { it.resultado == ResultadoSupervision.CUMPLE }
        return (cumplen.toDouble() / evaluados) * 100.0
    }

    fun progresoFormulario(): Double {
        if (_items.isEmpty()) return 0.0
        val respondidos = _items.count { it.fueEvaluado }
        return (respondidos.toDouble() / _items.size) * 100.0
    }

    fun itemsCriticos(): List<ItemSupervision> =
        _items.filter { it.resultado == ResultadoSupervision.NO_CUMPLE }

    fun generarReporte(): String =
        "Supervisión ${id} — ID Instalación: $instalacionId — Cumplimiento: ${"%.1f".format(porcentajeCumplimiento())}%"

    fun fechaFormateada(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
}

class ItemSupervision(
    val id: String,
    val descripcion: String,
    val categoria: CategoriaItem,
    var resultado: ResultadoSupervision = ResultadoSupervision.NO_APLICA,
    var observacion: String? = null,
    var fueEvaluado: Boolean = false
) {

    fun evaluar(nuevoResultado: ResultadoSupervision, obs: String? = null) {
        resultado = nuevoResultado
        observacion = obs
        fueEvaluado = true
    }

    fun limpiarEvaluacion() {
        resultado = ResultadoSupervision.NO_APLICA
        observacion = null
        fueEvaluado = false
    }

    fun guardar() {  }
}
