package com.grupo7poo2.digae.modelos

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Enumeraciones del dominio ────────────────────────────────────────────────

enum class TipoResiduo(val label: String, val peligroso: Boolean = false) {
    COMUN("Residuo Común"),
    RECICLABLE("Reciclable"),
    ORGANICO("Orgánico / Biodegradable"),
    PELIGROSO("Peligroso", peligroso = true),
    BIOINFECCIOSO("Bioinfeccioso", peligroso = true),
    ESPECIAL("Especial / RAEE", peligroso = true)
}

enum class EstadoBitacora(val label: String) {
    BORRADOR("Borrador"),
    EN_PROCESO("En proceso"),
    COMPLETADA("Completada"),
    ENVIADA("Enviada a DIGAE")
}

enum class UnidadPeso(val label: String) {
    KG("kg"),
    TON("ton"),
    LB("lb")
}

// ─── Clase principal: BitacoraResiduos ───────────────────────────────────────

class BitacoraResiduos(
    val id: String,
    val area: String,
    val empresa: String,
    val responsable: String,
    val fecha: Date = Date(),
    var estado: EstadoBitacora = EstadoBitacora.BORRADOR
) {
    private val _residuos = mutableListOf<Residuo>()

    /** Acceso de solo lectura (encapsulamiento POO) */
    val residuos: List<Residuo> get() = _residuos.toList()

    fun registrarSalida(residuo: Residuo) { _residuos.add(residuo) }

    fun obtenerHistorial(): List<Residuo> = _residuos.toList()

    fun totalResiduos(): Int = _residuos.size

    fun pesoTotalKg(): Double = _residuos.sumOf { it.pesoEnKg() }

    fun tienePeligrosos(): Boolean = _residuos.any { it.tipo.peligroso }

    fun residuosPorTipo(): Map<TipoResiduo, Int> =
        _residuos.groupBy { it.tipo }.mapValues { it.value.size }

    fun generarReporte(): String =
        "Bitácora $id — Área: $area — ${_residuos.size} residuos — ${"%,.2f".format(pesoTotalKg())} kg"

    fun fechaFormateada(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
}

// ─── Residuo individual ───────────────────────────────────────────────────────

class Residuo(
    val id: String,
    val descripcion: String,
    val tipo: TipoResiduo,
    var peso: Double,
    var unidad: UnidadPeso = UnidadPeso.KG,
    var observacion: String? = null
) {
    fun pesoEnKg(): Double = when (unidad) {
        UnidadPeso.KG  -> peso
        UnidadPeso.TON -> peso * 1000.0
        UnidadPeso.LB  -> peso * 0.453592
    }

    fun clasificar(nuevoTipo: TipoResiduo) { /* Reclasificación extensible */ }

    fun calcularVolumen(): Double = pesoEnKg() * 0.001
}
