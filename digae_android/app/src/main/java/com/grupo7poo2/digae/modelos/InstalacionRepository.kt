package com.grupo7poo2.digae.modelos

import com.grupo7poo2.digae.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object InstalacionRepository {
    private val _instalaciones = MutableStateFlow<List<Instalacion>>(emptyList())
    val instalaciones: StateFlow<List<Instalacion>> = _instalaciones.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        cargarDesdeRed()
    }

    fun cargarDesdeRed() {
        scope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerInstalaciones()
                if (response.isSuccessful) {
                    response.body()?.let { lista ->
                        _instalaciones.value = lista
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun agregar(instalacion: Instalacion) {
        scope.launch {
            try {
                val response = RetrofitClient.apiService.crearInstalacion(instalacion)
                if (response.isSuccessful) {
                    response.body()?.let { saved ->
                        _instalaciones.update { current -> current + saved }
                        ActividadRepository.registrar(
                            titulo = saved.nombre,
                            descripcion = "Instalación agregada al catálogo",
                            autor = "Administrador del Sistema",
                            modulo = ModuloApp.CRITICIDAD,
                            accion = TipoAccion.CREAR
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun eliminar(id: String) {
        scope.launch {
            try {
                val target = _instalaciones.value.find { it.id == id }
                val response = RetrofitClient.apiService.eliminarInstalacion(id)
                if (response.isSuccessful) {
                    _instalaciones.update { current -> current.filterNot { it.id == id } }
                    if (target != null) {
                        ActividadRepository.registrar(
                            titulo = target.nombre,
                            descripcion = "Instalación eliminada del catálogo",
                            autor = "Administrador del Sistema",
                            modulo = ModuloApp.CRITICIDAD,
                            accion = TipoAccion.ELIMINAR
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
