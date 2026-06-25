package com.grupo7poo2.digae.ui

import androidx.lifecycle.ViewModel
import com.grupo7poo2.digae.modelos.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.UUID

// ─── Estado de la UI ─────────────────────────────────────────────────────────
data class CriticidadUiState(
    val matrices: List<MatrizAspectos> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    // Estado de diálogos
    val mostrarNuevaMatriz: Boolean = false,
    val mostrarNuevoAspecto: Boolean = false,
    val matrizSeleccionadaId: String? = null,
    val aspectoEditandoId: String? = null
)

// ─── ViewModel ───────────────────────────────────────────────────────────────
class CriticidadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CriticidadUiState(isLoading = true))
    val uiState: StateFlow<CriticidadUiState> = _uiState.asStateFlow()

    private var estrategia: CalculoCriticidadStrategy = MotorCalculoMultiplicativo()

    // Lista mutable interna de matrices
    private val _matrices = mutableListOf<MatrizAspectos>()

    private val usuarioActual = "Ing. Carlos Medina"

    init {
        cargarDatosSemilla()
    }

    private fun cargarDatosSemilla() {
        _matrices.clear()
        _matrices.addAll(listOf(
            crearMatrizIngenieria(),
            crearMatrizMedicina(),
            crearMatrizAdministrativo()
        ))
        publicarMatrices()
    }

    // ─── CRUD: Matrices ───────────────────────────────────────────────────────

    fun abrirFormNuevaMatriz() {
        _uiState.update { it.copy(mostrarNuevaMatriz = true, matrizSeleccionadaId = null) }
    }

    fun abrirFormEditarMatriz(matrizId: String) {
        _uiState.update { it.copy(mostrarNuevaMatriz = true, matrizSeleccionadaId = matrizId) }
    }

    fun cerrarFormMatriz() {
        _uiState.update { it.copy(mostrarNuevaMatriz = false, matrizSeleccionadaId = null) }
    }

    fun guardarMatriz(area: String, actividad: String, estado: EstadoMatriz) {
        val idExistente = _uiState.value.matrizSeleccionadaId
        if (idExistente != null) {
            // Editar existente: reemplazar preservando aspectos
            val idx = _matrices.indexOfFirst { it.id == idExistente }
            if (idx >= 0) {
                val original = _matrices[idx]
                val nueva = MatrizAspectos(
                    id = original.id,
                    area = area.trim(),
                    actividad = actividad.trim(),
                    fechaRegistro = original.fechaRegistro,
                    estado = estado
                )
                original.aspectos.forEach { nueva.agregarAspecto(it) }
                _matrices[idx] = nueva
            }
        } else {
            // Nueva matriz
            val nueva = MatrizAspectos(
                id = "MAT-${_matrices.size + 1}".padStart(7, '0').replace("MAT-", "MAT-"),
                area = area.trim(),
                actividad = actividad.trim(),
                fechaRegistro = Date(),
                estado = estado
            )
            _matrices.add(nueva)
        }
        ActividadRepository.registrar(
            titulo = if (idExistente == null) "Nueva matriz — ${area.trim()}" else "Matriz editada — ${area.trim()}",
            descripcion = actividad.trim(),
            autor = usuarioActual,
            modulo = ModuloApp.CRITICIDAD,
            accion = if (idExistente == null) TipoAccion.CREAR else TipoAccion.EDITAR
        )
        publicarMatrices()
        cerrarFormMatriz()
    }

    fun eliminarMatriz(matrizId: String) {
        val m = _matrices.find { it.id == matrizId }
        _matrices.removeAll { it.id == matrizId }
        m?.let {
            ActividadRepository.registrar(
                titulo = "Matriz eliminada — ${it.area}",
                descripcion = it.actividad,
                autor = usuarioActual,
                modulo = ModuloApp.CRITICIDAD,
                accion = TipoAccion.ELIMINAR
            )
        }
        publicarMatrices()
    }

    // ─── CRUD: Aspectos ───────────────────────────────────────────────────────

    fun abrirFormNuevoAspecto(matrizId: String) {
        _uiState.update { it.copy(mostrarNuevoAspecto = true, matrizSeleccionadaId = matrizId, aspectoEditandoId = null) }
    }

    fun abrirFormEditarAspecto(matrizId: String, aspectoId: String) {
        _uiState.update { it.copy(mostrarNuevoAspecto = true, matrizSeleccionadaId = matrizId, aspectoEditandoId = aspectoId) }
    }

    fun cerrarFormAspecto() {
        _uiState.update { it.copy(mostrarNuevoAspecto = false, aspectoEditandoId = null) }
    }

    fun guardarAspecto(
        matrizId: String,
        descripcion: String,
        descripcionImpacto: String,
        tipo: TipoAspecto,
        gravedad: Int,
        severidad: Int,
        probabilidad: Int
    ) {
        val matriz = _matrices.find { it.id == matrizId } ?: return
        val aspectoEditandoId = _uiState.value.aspectoEditandoId

        val nuevoAspecto = AspectAmbiental(
            id = aspectoEditandoId ?: "ASP-${UUID.randomUUID().toString().take(6).uppercase()}",
            descripcion = descripcion.trim(),
            descripcionImpacto = descripcionImpacto.trim(),
            tipoAspecto = tipo,
            gravedad = gravedad,
            severidad = severidad,
            probabilidad = probabilidad,
            calculador = estrategia
        )

        // Reconstruir la matriz con la lista actualizada
        val aspectosActualizados = if (aspectoEditandoId != null) {
            // Reemplazar aspecto existente
            matriz.aspectos.map { if (it.id == aspectoEditandoId) nuevoAspecto else it }
        } else {
            // Agregar nuevo
            matriz.aspectos + nuevoAspecto
        }

        val matrizActualizada = MatrizAspectos(
            id = matriz.id,
            area = matriz.area,
            actividad = matriz.actividad,
            fechaRegistro = matriz.fechaRegistro,
            estado = matriz.estado
        )
        aspectosActualizados.forEach { matrizActualizada.agregarAspecto(it) }

        val idx = _matrices.indexOfFirst { it.id == matrizId }
        if (idx >= 0) _matrices[idx] = matrizActualizada

        publicarMatrices()
        cerrarFormAspecto()
        ActividadRepository.registrar(
            titulo = if (aspectoEditandoId == null) "Aspecto registrado — ${descripcion.trim()}" else "Aspecto editado — ${descripcion.trim()}",
            descripcion = "Matriz: ${_matrices.find { it.id == matrizId }?.area ?: matrizId}",
            autor = usuarioActual,
            modulo = ModuloApp.CRITICIDAD,
            accion = if (aspectoEditandoId == null) TipoAccion.CREAR else TipoAccion.EDITAR
        )
    }

    fun eliminarAspecto(matrizId: String, aspectoId: String) {
        val matriz = _matrices.find { it.id == matrizId } ?: return
        val asp = matriz.aspectos.find { it.id == aspectoId }
        val nuevaMatriz = MatrizAspectos(
            id = matriz.id, area = matriz.area, actividad = matriz.actividad,
            fechaRegistro = matriz.fechaRegistro, estado = matriz.estado
        )
        matriz.aspectos.filter { it.id != aspectoId }.forEach { nuevaMatriz.agregarAspecto(it) }
        val idx = _matrices.indexOfFirst { it.id == matrizId }
        if (idx >= 0) _matrices[idx] = nuevaMatriz
        asp?.let {
            ActividadRepository.registrar(
                titulo = "Aspecto eliminado — ${it.descripcion}",
                descripcion = "Matriz: ${matriz.area}",
                autor = usuarioActual,
                modulo = ModuloApp.CRITICIDAD,
                accion = TipoAccion.ELIMINAR
            )
        }
        publicarMatrices()
    }

    fun obtenerMatriz(matrizId: String): MatrizAspectos? = _matrices.find { it.id == matrizId }

    fun obtenerAspecto(matrizId: String, aspectoId: String): AspectAmbiental? =
        _matrices.find { it.id == matrizId }?.aspectos?.find { it.id == aspectoId }

    fun cambiarEstrategia(nueva: CalculoCriticidadStrategy) {
        estrategia = nueva
        cargarDatosSemilla()
    }

    private fun publicarMatrices() {
        _uiState.update { it.copy(matrices = _matrices.toList(), isLoading = false) }
    }

    // ─── Datos semilla ────────────────────────────────────────────────────────

    private fun crearMatrizIngenieria(): MatrizAspectos {
        val m = MatrizAspectos("MAT-001", "Facultad de Ingeniería",
            "Laboratorios de química y manufactura", Date(), EstadoMatriz.APROBADA)
        m.agregarAspecto(AspectAmbiental("ASP-001", "Emisión de vapores químicos",
            "Contaminación del aire interior y exterior", TipoAspecto.AIRE,
            4, 5, 3, estrategia).also {
            it.asignarControl(ControlOperacional("CT-001","Extractor de aire forzado",50.0,"Ingeniería")) })
        m.agregarAspecto(AspectAmbiental("ASP-002", "Vertido de residuos líquidos",
            "Contaminación hídrica", TipoAspecto.AGUA, 5, 5, 2, estrategia).also {
            it.asignarControl(ControlOperacional("CT-002","Trampa de grasas y neutralizador",40.0,"Ingeniería")) })
        m.agregarAspecto(AspectAmbiental("ASP-003", "Consumo energético en stand-by",
            "Aumento de huella de carbono", TipoAspecto.ENERGIA, 2, 3, 5, estrategia))
        return m
    }

    private fun crearMatrizMedicina(): MatrizAspectos {
        val m = MatrizAspectos("MAT-002", "Facultad de Medicina",
            "Prácticas clínicas y manejo de biológicos", Date(), EstadoMatriz.EN_REVISION)
        m.agregarAspecto(AspectAmbiental("ASP-004", "Generación de residuos bioinfecciosos",
            "Riesgo sanitario y contaminación de suelo", TipoAspecto.RESIDUOS,
            5, 5, 4, estrategia).also {
            it.asignarControl(ControlOperacional("CT-003","Recolección diferenciada y autoclave",80.0,"Administrativa")) })
        m.agregarAspecto(AspectAmbiental("ASP-005", "Uso de formaldehído en conservación",
            "Afectación a la salud por exposición crónica", TipoAspecto.AIRE, 5, 4, 3, estrategia))
        return m
    }

    private fun crearMatrizAdministrativo(): MatrizAspectos {
        val m = MatrizAspectos("MAT-003", "Edificio Administrativo",
            "Operación general de oficinas", Date(), EstadoMatriz.BORRADOR)
        m.agregarAspecto(AspectAmbiental("ASP-006", "Consumo de papel y plásticos",
            "Generación de residuos sólidos", TipoAspecto.RESIDUOS, 2, 2, 5, estrategia))
        m.agregarAspecto(AspectAmbiental("ASP-007", "Consumo de agua en sanitarios",
            "Presión sobre el sistema hídrico", TipoAspecto.AGUA, 2, 3, 4, estrategia))
        return m
    }
}
