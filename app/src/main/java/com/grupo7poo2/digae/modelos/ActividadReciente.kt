package com.grupo7poo2.digae.modelos

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Enums del módulo de actividad ────────────────────────────────────────────

enum class ModuloApp(val label: String) {
    CRITICIDAD("Criticidad Ambiental"),
    SUPERVISION("Supervisión en Campo"),
    TRAZABILIDAD("Trazabilidad de Residuos")
}

enum class TipoAccion(val label: String) {
    CREAR("creó"),
    EDITAR("editó"),
    ELIMINAR("eliminó")
}

// ─── Modelo de actividad reciente ─────────────────────────────────────────────

data class ActividadReciente(
    val id: String,
    val titulo: String,           // Nombre del recurso (ej. "Matriz MAT-001")
    val descripcion: String,      // Descripción corta (ej. "Facultad de Ingeniería")
    val autor: String,
    val timestamp: Date = Date(),
    val modulo: ModuloApp,
    val tipoAccion: TipoAccion
) {
    /** Texto relativo del timestamp: "Hace 2 min", "Hace 1 h", etc. */
    fun tiempoRelativo(): String {
        val diffMs = Date().time - timestamp.time
        val mins   = diffMs / 60_000
        val horas  = mins / 60
        val dias   = horas / 24
        return when {
            mins  < 1    -> "Justo ahora"
            mins  < 60   -> "Hace $mins min"
            horas < 24   -> "Hace $horas h"
            dias  < 7    -> "Hace $dias día${if (dias > 1) "s" else ""}"
            else         -> SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(timestamp)
        }
    }

    /** Texto corto para mostrar en la tarjeta del feed */
    fun textoFeed(): String = "${tipoAccion.label.replaceFirstChar { it.uppercase() }}: $titulo"
}
