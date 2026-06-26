package com.grupo7poo2.digae.modelos

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

object ActividadRepository {

    private const val MAX_ACTIVIDADES = 50

    private val _actividades = MutableStateFlow<List<ActividadReciente>>(emptyList())
    val actividades: StateFlow<List<ActividadReciente>> = _actividades.asStateFlow()

    fun registrar(actividad: ActividadReciente) {
        _actividades.update { lista ->
            val nueva = listOf(actividad) + lista
            if (nueva.size > MAX_ACTIVIDADES) nueva.take(MAX_ACTIVIDADES) else nueva
        }
    }

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

    fun limpiar() { _actividades.update { emptyList() } }
}
