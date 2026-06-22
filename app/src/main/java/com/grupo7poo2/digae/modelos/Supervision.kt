package com.grupo7poo2.digae.modelos

import java.util.Date

class Supervision(
    val id: String,
    val fecha: Date,
    val supervisor: Usuario,
    val area: String,
    var estado: String
) {
    private val items = mutableListOf<ItemSupervision>()

    fun registrarHallazgo(item: ItemSupervision) {
        items.add(item)
    }

    fun generarReporte(): String {
        return "Reporte de Supervisión - ID: $id - Estado: $estado"
    }

    fun exportar() {
        // Exportación a PDF/Excel
    }
}

class ItemSupervision(
    val id: String,
    val categoria: String,
    val descripcion: String,
    var resultado: ResultadoSupervision
) {
    fun evaluar(nuevoResultado: ResultadoSupervision) {
        resultado = nuevoResultado
    }

    fun guardar() {
        // Lógica de guardado
    }
}

enum class ResultadoSupervision {
    CUMPLE, NO_CUMPLE, NO_APLICA
}
