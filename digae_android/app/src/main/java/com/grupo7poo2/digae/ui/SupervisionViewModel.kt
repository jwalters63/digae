package com.grupo7poo2.digae.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo7poo2.digae.network.auth.SessionManager
import com.grupo7poo2.digae.modelos.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.launch

data class SupervisionUiState(
    val supervisiones: List<Supervision> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val mostrarNuevaSupervision: Boolean = false,
    val mostrarNuevoItem: Boolean = false,
    val supervisionSeleccionadaId: String? = null,
    val itemEditandoId: String? = null
)

class SupervisionViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    val userRole = sessionManager.fetchUserRole()

    private val _uiState = MutableStateFlow(SupervisionUiState(isLoading = true))
    val uiState: StateFlow<SupervisionUiState> = _uiState.asStateFlow()

    private val _supervisiones = mutableListOf<Supervision>()
    private val usuarioActual = "Usuario Actual"

    init {
        cargarDatosDesdeRed()
    }

    fun cargarDatosDesdeRed() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = com.grupo7poo2.digae.network.RetrofitClient.apiService.obtenerSupervisiones()
                if (response.isSuccessful) {
                    val dtos = response.body() ?: emptyList()
                    _supervisiones.clear()
                    _supervisiones.addAll(dtos.map { dto ->
                        val df = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        val fechaParsed = try {
                            df.parse(dto.fechaInspeccion) ?: Date()
                        } catch (e: Exception) { Date() }

                        Supervision(
                            id = dto.id.toString(),
                            instalacionId = dto.areaInspeccionada,
                            tipo = TipoSupervision.INFRAESTRUCTURA, 
                            fecha = fechaParsed,
                            supervisor = dto.inspectorNombreCompleto ?: "Sin supervisor",
                            estado = EstadoSupervision.COMPLETADA
                        ).also { sup ->
                            dto.items?.forEach { itemDto ->
                                sup.agregarItem(ItemSupervision(
                                    id = UUID.randomUUID().toString(),
                                    descripcion = itemDto.descripcion,
                                    categoria = CategoriaItem.INSTALACIONES,
                                    resultado = if (itemDto.resultado == "CUMPLE") ResultadoSupervision.CUMPLE else ResultadoSupervision.NO_CUMPLE
                                ))
                            }
                        }
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                publicar()
            }
        }
    }

    fun abrirFormNuevaSupervision() {
        _uiState.update { it.copy(mostrarNuevaSupervision = true, supervisionSeleccionadaId = null) }
    }

    fun abrirFormEditarSupervision(id: String) {
        _uiState.update { it.copy(mostrarNuevaSupervision = true, supervisionSeleccionadaId = id) }
    }

    fun cerrarFormSupervision() {
        _uiState.update { it.copy(mostrarNuevaSupervision = false, supervisionSeleccionadaId = null) }
    }

    fun guardarSupervision(instalacionId: String, tipo: TipoSupervision, estado: EstadoSupervision, supervisor: String) {
        val idExistente = _uiState.value.supervisionSeleccionadaId
        val esNueva = idExistente == null

        viewModelScope.launch {
            try {
                if (esNueva) {
                    val request = com.grupo7poo2.digae.network.dto.SupervisionRequestDTO(
                        fechaInspeccion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date()),
                        areaInspeccionada = instalacionId,
                        calificacionGeneral = 10.0,
                        observaciones = null,
                        inspectorId = sessionManager.fetchUserId().takeIf { it != -1L } ?: 1L,
                        items = emptyList() 
                    )
                    val response = com.grupo7poo2.digae.network.RetrofitClient.apiService.crearSupervision(request)
                    if (!response.isSuccessful) {
                        println("Error al crear supervisión: ${response.code()}")
                    }
                }

                ActividadRepository.registrar(
                    titulo = if (esNueva) "Nueva supervisión — ${instalacionId.trim()}" else "Supervisión editada — ${instalacionId.trim()}",
                    descripcion = tipo.label,
                    autor = supervisor.trim().ifBlank { usuarioActual },
                    modulo = ModuloApp.SUPERVISION,
                    accion = if (esNueva) TipoAccion.CREAR else TipoAccion.EDITAR
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cargarDatosDesdeRed()
                cerrarFormSupervision()
            }
        }
    }

    fun eliminarSupervision(id: String) {
        val s = _supervisiones.find { it.id == id }
        _supervisiones.removeAll { it.id == id }
        s?.let {
            ActividadRepository.registrar(
                titulo = "Supervisión eliminada — ${it.instalacionId}",
                descripcion = it.tipo.label,
                autor = usuarioActual,
                modulo = ModuloApp.SUPERVISION,
                accion = TipoAccion.ELIMINAR
            )
        }
        publicar()
    }

    fun abrirFormNuevoItem(supervisionId: String) {
        _uiState.update { it.copy(mostrarNuevoItem = true, supervisionSeleccionadaId = supervisionId, itemEditandoId = null) }
    }

    fun abrirFormEditarItem(supervisionId: String, itemId: String) {
        _uiState.update { it.copy(mostrarNuevoItem = true, supervisionSeleccionadaId = supervisionId, itemEditandoId = itemId) }
    }

    fun cerrarFormItem() {
        _uiState.update { it.copy(mostrarNuevoItem = false, itemEditandoId = null) }
    }

    fun guardarItem(supervisionId: String, descripcion: String, categoria: CategoriaItem) {
        val supervision = _supervisiones.find { it.id == supervisionId } ?: return
        val itemEditandoId = _uiState.value.itemEditandoId

        val nuevoItem = ItemSupervision(
            id = itemEditandoId ?: "ITEM-${UUID.randomUUID().toString().take(6).uppercase()}",
            descripcion = descripcion.trim(),
            categoria = categoria
        )

        val itemsActualizados = if (itemEditandoId != null) {
            supervision.items.map { if (it.id == itemEditandoId) nuevoItem.also { n ->

                val original = supervision.items.find { i -> i.id == itemEditandoId }
                if (original?.fueEvaluado == true) n.evaluar(original.resultado, original.observacion)
            } else it }
        } else {
            supervision.items + nuevoItem
        }

        reconstruirSupervision(supervision, itemsActualizados)
        ActividadRepository.registrar(
            titulo = if (itemEditandoId == null) "Ítem registrado — ${descripcion.trim()}" else "Ítem editado — ${descripcion.trim()}",
            descripcion = "Supervisión: ${supervision.instalacionId}",
            autor = usuarioActual,
            modulo = ModuloApp.SUPERVISION,
            accion = if (itemEditandoId == null) TipoAccion.CREAR else TipoAccion.EDITAR
        )
        publicar()
        cerrarFormItem()
    }

    fun eliminarItem(supervisionId: String, itemId: String) {
        val supervision = _supervisiones.find { it.id == supervisionId } ?: return
        val item = supervision.items.find { it.id == itemId }
        reconstruirSupervision(supervision, supervision.items.filter { it.id != itemId })
        item?.let {
            ActividadRepository.registrar(
                titulo = "Ítem eliminado — ${it.descripcion}",
                descripcion = "Supervisión: ${supervision.instalacionId}",
                autor = usuarioActual,
                modulo = ModuloApp.SUPERVISION,
                accion = TipoAccion.ELIMINAR
            )
        }
        publicar()
    }

    fun evaluarItem(supervisionId: String, itemId: String, resultado: ResultadoSupervision, observacion: String? = null) {
        val supervision = _supervisiones.find { it.id == supervisionId } ?: return
        val itemsActualizados = supervision.items.map {
            if (it.id == itemId) {
                val copia = ItemSupervision(it.id, it.descripcion, it.categoria, it.resultado, it.observacion, it.fueEvaluado)
                copia.evaluar(resultado, observacion)
                copia
            } else it
        }
        reconstruirSupervision(supervision, itemsActualizados)
        publicar()
    }

    fun actualizarObservacion(supervisionId: String, itemId: String, observacion: String) {
        val supervision = _supervisiones.find { it.id == supervisionId } ?: return
        val itemsActualizados = supervision.items.map {
            if (it.id == itemId) {
                val copia = ItemSupervision(it.id, it.descripcion, it.categoria, it.resultado, observacion, it.fueEvaluado)
                copia
            } else it
        }
        reconstruirSupervision(supervision, itemsActualizados)
        publicar()
    }

    fun obtenerSupervision(id: String): Supervision? = _supervisiones.find { it.id == id }

    fun obtenerItem(supervisionId: String, itemId: String): ItemSupervision? =
        _supervisiones.find { it.id == supervisionId }?.items?.find { it.id == itemId }

    private fun reconstruirSupervision(original: Supervision, items: List<ItemSupervision>) {
        val nueva = Supervision(original.id, original.instalacionId, original.tipo, original.fecha, original.supervisor, original.estado)
        items.forEach { nueva.agregarItem(it) }
        val idx = _supervisiones.indexOfFirst { it.id == original.id }
        if (idx >= 0) _supervisiones[idx] = nueva
    }

    private fun publicar() {
        _uiState.update { it.copy(supervisiones = _supervisiones.toList(), isLoading = false) }
    }

    private fun crearSupervisionInfraestructura(): Supervision {
        val s = Supervision("SUP-001", "Facultad de Ciencias", TipoSupervision.INFRAESTRUCTURA,
            Date(), "Ing. Andrea Vargas", EstadoSupervision.COMPLETADA)
        listOf(
            ItemSupervision("IT-001", "Canaletas de drenaje sin obstrucciones", CategoriaItem.INSTALACIONES).also { it.evaluar(ResultadoSupervision.CUMPLE) },
            ItemSupervision("IT-002", "Señalización de áreas de residuos visible", CategoriaItem.INSTALACIONES).also { it.evaluar(ResultadoSupervision.CUMPLE) },
            ItemSupervision("IT-003", "Extintores con fecha de vigencia actualizada", CategoriaItem.EQUIPOS).also { it.evaluar(ResultadoSupervision.NO_CUMPLE, "Extintor del piso 2 vencido desde enero") },
            ItemSupervision("IT-004", "Bitácora de mantenimiento al día", CategoriaItem.DOCUMENTACION).also { it.evaluar(ResultadoSupervision.CUMPLE) },
            ItemSupervision("IT-005", "Personal capacitado en manejo de emergencias", CategoriaItem.PERSONAL).also { it.evaluar(ResultadoSupervision.NO_APLICA) }
        ).forEach { s.agregarItem(it) }
        return s
    }

    private fun crearSupervisionEnergia(): Supervision {
        val s = Supervision("SUP-002", "Bloque Administrativo Central", TipoSupervision.ENERGIA,
            Date(), "Lic. Marco Torres", EstadoSupervision.EN_PROGRESO)
        listOf(
            ItemSupervision("IT-006", "Medidores de consumo energético funcionando", CategoriaItem.EQUIPOS).also { it.evaluar(ResultadoSupervision.CUMPLE) },
            ItemSupervision("IT-007", "Luminarias con tecnología LED instaladas", CategoriaItem.INSTALACIONES).also { it.evaluar(ResultadoSupervision.NO_CUMPLE, "Aún 40% con lámparas fluorescentes") },
            ItemSupervision("IT-008", "Procedimiento de apagado nocturno documentado", CategoriaItem.PROCEDIMIENTOS),
            ItemSupervision("IT-009", "Sensores de presencia en pasillos activos", CategoriaItem.EQUIPOS),
            ItemSupervision("IT-010", "Informe mensual de consumo enviado a DIGAE", CategoriaItem.DOCUMENTACION).also { it.evaluar(ResultadoSupervision.CUMPLE) }
        ).forEach { s.agregarItem(it) }
        return s
    }

    private fun crearSupervisionResiduos(): Supervision {
        val s = Supervision("SUP-003", "Laboratorio de Biología Marina", TipoSupervision.RESIDUOS,
            Date(), "Dr. Sofía Méndez", EstadoSupervision.PROGRAMADA)
        listOf(
            ItemSupervision("IT-011", "Contenedores diferenciados por tipo de residuo", CategoriaItem.INSTALACIONES),
            ItemSupervision("IT-012", "Manifiesto de residuos peligrosos actualizado", CategoriaItem.DOCUMENTACION),
            ItemSupervision("IT-013", "Empresa recolectora certificada contratada", CategoriaItem.PROCEDIMIENTOS),
            ItemSupervision("IT-014", "Personal con EPP para manejo de residuos", CategoriaItem.PERSONAL)
        ).forEach { s.agregarItem(it) }
        return s
    }
}
