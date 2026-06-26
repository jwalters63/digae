package com.grupo7poo2.digae.modelos

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * Repositorio singleton de actividad reciente.
 *
 * Patrón Repository: centraliza la gestión del feed de actividad
 * para que todos los ViewModels publiquen eventos a un único origen
 * de verdad observable por el Dashboard.
 *
 * El Dashboard lo lee via StateFlow, garantizando reactividad sin
 * acoplar directamente los módulos entre sí.
 */
object ActividadRepository {

    private const val MAX_ACTIVIDADES = 50

    private val _actividades = MutableStateFlow<List<ActividadReciente>>(emptyList())
    val actividades: StateFlow<List<ActividadReciente>> = _actividades.asStateFlow()

    /** Registra un nuevo evento al inicio de la lista (más reciente primero). */
    fun registrar(actividad: ActividadReciente) {
        _actividades.update { lista ->
            val nueva = listOf(actividad) + lista
            if (nueva.size > MAX_ACTIVIDADES) nueva.take(MAX_ACTIVIDADES) else nueva
        }
    }

    /** Atajo para registrar con los parámetros más comunes. */
    fun registrar(
        titulo: String,
        descripcion: String,
        autor: String,
        modulo: ModuloApp,
        accion: TipoAccion
    ) {
        registrar(
            ActividadReciente(
                id = UUID.randomUUID().toString(),
                titulo = titulo,
                descripcion = descripcion,
                autor = autor,
                modulo = modulo,
                tipoAccion = accion
            )
        )
    }

    /** Limpia el historial (útil en tests o reset de sesión). */
    fun limpiar() { _actividades.update { emptyList() } }
}
