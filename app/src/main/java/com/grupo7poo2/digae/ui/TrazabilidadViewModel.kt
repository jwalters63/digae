package com.grupo7poo2.digae.ui

import androidx.lifecycle.ViewModel
import com.grupo7poo2.digae.modelos.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.UUID

// ─── Estado de la UI ──────────────────────────────────────────────────────────
data class TrazabilidadUiState(
    val bitacoras: List<BitacoraResiduos> = emptyList(),
    val isLoading: Boolean = false,
    val mostrarNuevaBitacora: Boolean = false,
    val mostrarNuevoResiduo: Boolean = false,
    val bitacoraSeleccionadaId: String? = null,
    val residuoEditandoId: String? = null
)

// ─── ViewModel ────────────────────────────────────────────────────────────────
class TrazabilidadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TrazabilidadUiState(isLoading = true))
    val uiState: StateFlow<TrazabilidadUiState> = _uiState.asStateFlow()

    private val _bitacoras = mutableListOf<BitacoraResiduos>()

    // Nombre de usuario en sesión (simplificado)
    private val usuarioActual = "Ing. Andrea Vargas"

    init { cargarDatosSemilla() }

    private fun cargarDatosSemilla() {
        _bitacoras.clear()
        _bitacoras.addAll(listOf(
            crearBitacoraIngenieria(),
            crearBitacoraMedicina(),
            crearBitacoraAdministrativo()
        ))
        publicar()
    }

    // ─── CRUD: Bitácoras ──────────────────────────────────────────────────────

    fun abrirFormNuevaBitacora() {
        _uiState.update { it.copy(mostrarNuevaBitacora = true, bitacoraSeleccionadaId = null) }
    }

    fun abrirFormEditarBitacora(id: String) {
        _uiState.update { it.copy(mostrarNuevaBitacora = true, bitacoraSeleccionadaId = id) }
    }

    fun cerrarFormBitacora() {
        _uiState.update { it.copy(mostrarNuevaBitacora = false, bitacoraSeleccionadaId = null) }
    }

    fun guardarBitacora(instalacionId: String, empresa: String, responsable: String, estado: EstadoBitacora) {
        val idExistente = _uiState.value.bitacoraSeleccionadaId
        val esNueva = idExistente == null

        if (idExistente != null) {
            val idx = _bitacoras.indexOfFirst { it.id == idExistente }
            if (idx >= 0) {
                val original = _bitacoras[idx]
                val nueva = BitacoraResiduos(original.id, instalacionId.trim(), empresa.trim(),
                    responsable.trim(), original.fecha, estado)
                original.residuos.forEach { nueva.registrarSalida(it) }
                _bitacoras[idx] = nueva
            }
        } else {
            _bitacoras.add(BitacoraResiduos(
                id = "BIT-${UUID.randomUUID().toString().take(5).uppercase()}",
                instalacionId = instalacionId.trim(), empresa = empresa.trim(),
                responsable = responsable.trim(), fecha = Date(), estado = estado
            ))
        }

        ActividadRepository.registrar(
            titulo = if (esNueva) "Nueva bitácora — $instalacionId" else "Bitácora editada — $instalacionId",
            descripcion = "Empresa: $empresa",
            autor = usuarioActual,
            modulo = ModuloApp.TRAZABILIDAD,
            accion = if (esNueva) TipoAccion.CREAR else TipoAccion.EDITAR
        )

        publicar()
        cerrarFormBitacora()
    }

    fun eliminarBitacora(id: String) {
        val b = _bitacoras.find { it.id == id }
        _bitacoras.removeAll { it.id == id }
        b?.let {
            ActividadRepository.registrar(
                titulo = "Bitácora eliminada — ${it.instalacionId}",
                descripcion = it.empresa,
                autor = usuarioActual,
                modulo = ModuloApp.TRAZABILIDAD,
                accion = TipoAccion.ELIMINAR
            )
        }
        publicar()
    }

    // ─── CRUD: Residuos ───────────────────────────────────────────────────────

    fun abrirFormNuevoResiduo(bitacoraId: String) {
        _uiState.update { it.copy(mostrarNuevoResiduo = true, bitacoraSeleccionadaId = bitacoraId, residuoEditandoId = null) }
    }

    fun abrirFormEditarResiduo(bitacoraId: String, residuoId: String) {
        _uiState.update { it.copy(mostrarNuevoResiduo = true, bitacoraSeleccionadaId = bitacoraId, residuoEditandoId = residuoId) }
    }

    fun cerrarFormResiduo() {
        _uiState.update { it.copy(mostrarNuevoResiduo = false, residuoEditandoId = null) }
    }

    fun guardarResiduo(
        bitacoraId: String, descripcion: String, tipo: TipoResiduo,
        peso: Double, unidad: UnidadPeso, observacion: String?
    ) {
        val bitacora = _bitacoras.find { it.id == bitacoraId } ?: return
        val editandoId = _uiState.value.residuoEditandoId
        val esNuevo = editandoId == null

        val nuevo = Residuo(
            id = editandoId ?: "RES-${UUID.randomUUID().toString().take(6).uppercase()}",
            descripcion = descripcion.trim(), tipo = tipo,
            peso = peso, unidad = unidad, observacion = observacion?.ifBlank { null }
        )
        val lista = if (editandoId != null)
            bitacora.residuos.map { if (it.id == editandoId) nuevo else it }
        else bitacora.residuos + nuevo

        reconstruirBitacora(bitacora, lista)

        ActividadRepository.registrar(
            titulo = if (esNuevo) "Residuo registrado — ${tipo.label}" else "Residuo editado — ${tipo.label}",
            descripcion = "$descripcion · ${bitacora.instalacionId}",
            autor = usuarioActual,
            modulo = ModuloApp.TRAZABILIDAD,
            accion = if (esNuevo) TipoAccion.CREAR else TipoAccion.EDITAR
        )

        publicar()
        cerrarFormResiduo()
    }

    fun eliminarResiduo(bitacoraId: String, residuoId: String) {
        val bitacora = _bitacoras.find { it.id == bitacoraId } ?: return
        val res = bitacora.residuos.find { it.id == residuoId }
        reconstruirBitacora(bitacora, bitacora.residuos.filter { it.id != residuoId })
        res?.let {
            ActividadRepository.registrar(
                titulo = "Residuo eliminado — ${it.tipo.label}",
                descripcion = "${it.descripcion} · ${bitacora.instalacionId}",
                autor = usuarioActual,
                modulo = ModuloApp.TRAZABILIDAD,
                accion = TipoAccion.ELIMINAR
            )
        }
        publicar()
    }

    fun obtenerBitacora(id: String): BitacoraResiduos? = _bitacoras.find { it.id == id }

    fun obtenerResiduo(bitacoraId: String, residuoId: String): Residuo? =
        _bitacoras.find { it.id == bitacoraId }?.residuos?.find { it.id == residuoId }

    private fun reconstruirBitacora(original: BitacoraResiduos, residuos: List<Residuo>) {
        val nueva = BitacoraResiduos(original.id, original.instalacionId, original.empresa,
            original.responsable, original.fecha, original.estado)
        residuos.forEach { nueva.registrarSalida(it) }
        val idx = _bitacoras.indexOfFirst { it.id == original.id }
        if (idx >= 0) _bitacoras[idx] = nueva
    }

    private fun publicar() {
        _uiState.update { it.copy(bitacoras = _bitacoras.toList(), isLoading = false) }
    }

    // ─── Datos semilla ────────────────────────────────────────────────────────

    private fun crearBitacoraIngenieria(): BitacoraResiduos {
        val b = BitacoraResiduos("BIT-001", "Facultad de Ingeniería",
            "EcoGest S.A. de C.V.", "Ing. Andrea Vargas", Date(), EstadoBitacora.COMPLETADA)
        b.registrarSalida(Residuo("RES-001", "Solventes y reactivos vencidos", TipoResiduo.PELIGROSO, 45.0, UnidadPeso.KG, "Residuos de laboratorio química orgánica"))
        b.registrarSalida(Residuo("RES-002", "Cartón y papel de oficinas", TipoResiduo.RECICLABLE, 120.0, UnidadPeso.KG))
        b.registrarSalida(Residuo("RES-003", "Residuos generales de cafetería", TipoResiduo.ORGANICO, 60.0, UnidadPeso.KG))
        return b
    }

    private fun crearBitacoraMedicina(): BitacoraResiduos {
        val b = BitacoraResiduos("BIT-002", "Facultad de Medicina",
            "BioHazard Disposal S.A.", "Dr. Sofía Méndez", Date(), EstadoBitacora.EN_PROCESO)
        b.registrarSalida(Residuo("RES-004", "Material bioinfeccioso de prácticas clínicas", TipoResiduo.BIOINFECCIOSO, 15.0, UnidadPeso.KG, "Incluye guantes, gasas y material punzocortante"))
        b.registrarSalida(Residuo("RES-005", "Equipos electrónicos en desuso (RAEE)", TipoResiduo.ESPECIAL, 80.0, UnidadPeso.KG, "Computadoras y monitores de laboratorio"))
        b.registrarSalida(Residuo("RES-006", "Formaldehído usado en conservación", TipoResiduo.PELIGROSO, 8.5, UnidadPeso.KG))
        return b
    }

    private fun crearBitacoraAdministrativo(): BitacoraResiduos {
        val b = BitacoraResiduos("BIT-003", "Edificio Administrativo",
            "ReciclaUni", "Lic. Marco Torres", Date(), EstadoBitacora.BORRADOR)
        b.registrarSalida(Residuo("RES-007", "Papel de archivo y documentos", TipoResiduo.RECICLABLE, 200.0, UnidadPeso.KG))
        b.registrarSalida(Residuo("RES-008", "Residuos de jardinería", TipoResiduo.ORGANICO, 35.0, UnidadPeso.KG))
        return b
    }
}
