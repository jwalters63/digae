package com.grupo7poo2.digae.modelos

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object InstalacionRepository {
    private val _instalaciones = MutableStateFlow<List<Instalacion>>(emptyList())
    val instalaciones: StateFlow<List<Instalacion>> = _instalaciones.asStateFlow()

    init {
        // Datos semilla iniciales
        _instalaciones.value = listOf(
            Instalacion(nombre = "Facultad de Ingeniería", tipo = TipoInstalacion.FACULTAD, ubicacion = "Campus Central, Bloque A"),
            Instalacion(nombre = "Facultad de Medicina", tipo = TipoInstalacion.FACULTAD, ubicacion = "Campus Central, Bloque C"),
            Instalacion(nombre = "Laboratorio de Química", tipo = TipoInstalacion.LABORATORIO, ubicacion = "Bloque D, Piso 1"),
            Instalacion(nombre = "Biblioteca Central", tipo = TipoInstalacion.BIBLIOTECA, ubicacion = "Plaza Principal")
        )
    }

    fun agregar(instalacion: Instalacion) {
        _instalaciones.update { current ->
            current + instalacion
        }
        ActividadRepository.registrar(
            titulo = instalacion.nombre,
            descripcion = "Instalación agregada al catálogo",
            autor = "Administrador del Sistema", // hardcoded para el ejemplo
            modulo = ModuloApp.CRITICIDAD, // Podemos mapearlo de forma neutral
            tipoAccion = TipoAccion.CREAR
        )
    }

    fun eliminar(id: String) {
        val target = _instalaciones.value.find { it.id == id }
        _instalaciones.update { current ->
            current.filterNot { it.id == id }
        }
        if (target != null) {
            ActividadRepository.registrar(
                titulo = target.nombre,
                descripcion = "Instalación eliminada del catálogo",
                autor = "Administrador del Sistema",
                modulo = ModuloApp.CRITICIDAD,
                tipoAccion = TipoAccion.ELIMINAR
            )
        }
    }
}
