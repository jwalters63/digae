package com.grupo7poo2.digae.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.grupo7poo2.digae.network.auth.SessionManager
import com.grupo7poo2.digae.modelos.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.UUID
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class CriticidadUiState(
    val matrices: List<MatrizAspectos> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val mostrarNuevaMatriz: Boolean = false,
    val mostrarNuevoAspecto: Boolean = false,
    val matrizSeleccionadaId: String? = null,
    val aspectoEditandoId: String? = null
)

class CriticidadViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    val userRole = sessionManager.fetchUserRole()

    private val _uiState = MutableStateFlow(CriticidadUiState(isLoading = true))
    val uiState: StateFlow<CriticidadUiState> = _uiState.asStateFlow()

    private var estrategia: CalculoCriticidadStrategy = MotorCalculoMultiplicativo()

    private val _matrices = mutableListOf<MatrizAspectos>()

    private val usuarioActual = "Usuario Actual"

    init {
        cargarDatosDesdeRed()
    }

    fun cargarDatosDesdeRed() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = com.grupo7poo2.digae.network.RetrofitClient.apiService.obtenerMatrices()
                if (response.isSuccessful) {
                    val dtos = response.body() ?: emptyList()
                    _matrices.clear()
                    _matrices.addAll(dtos.map { dto ->
                        val df = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        val fechaParsed = try {
                            df.parse(dto.fechaEvaluacion ?: "") ?: Date()
                        } catch (e: Exception) { Date() }

                        val m = MatrizAspectos(
                            id = dto.id.toString(),
                            instalacionId = dto.facultadNombre ?: "Facultad Desconocida",
                            actividad = dto.nombre,
                            fechaRegistro = fechaParsed,
                            estado = EstadoMatriz.APROBADA 
                        )
                        dto.aspectos?.forEach { aspDto ->
                            val asp = AspectAmbiental(
                                id = aspDto.id.toString(),
                                descripcion = aspDto.descripcion,
                                descripcionImpacto = "Impacto General",
                                tipoAspecto = TipoAspecto.AIRE, 
                                gravedad = aspDto.gravedad,
                                severidad = aspDto.severidad,
                                probabilidad = aspDto.probabilidad,
                                calculador = estrategia
                            )
                            aspDto.controles?.forEach { ctrlDto ->
                                asp.asignarControl(ControlOperacional(
                                    id = ctrlDto.id.toString(),
                                    descripcion = ctrlDto.descripcion,
                                    nivelCriticidadRequerido = 0.0,
                                    tipoControl = "Sistema"
                                ))
                            }
                            m.agregarAspecto(asp)
                        }
                        m
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                publicarMatrices()
            }
        }
    }

    fun abrirFormNuevaMatriz() {
        _uiState.update { it.copy(mostrarNuevaMatriz = true, matrizSeleccionadaId = null) }
    }

    fun abrirFormEditarMatriz(matrizId: String) {
        _uiState.update { it.copy(mostrarNuevaMatriz = true, matrizSeleccionadaId = matrizId) }
    }

    fun cerrarFormMatriz() {
        _uiState.update { it.copy(mostrarNuevaMatriz = false, matrizSeleccionadaId = null) }
    }

    fun guardarMatriz(instalacionId: String, actividad: String, estado: EstadoMatriz) {
        val idExistente = _uiState.value.matrizSeleccionadaId
        val esNueva = idExistente == null

        viewModelScope.launch {
            try {
                if (esNueva) {
                    val request = com.grupo7poo2.digae.network.dto.MatrizAspectosRequestDTO(
                        nombre = actividad,
                        fechaEvaluacion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date()),
                        facultadId = sessionManager.fetchUserFacultadId().takeIf { it != -1L } ?: 1L,
                        creadoPorId = sessionManager.fetchUserId().takeIf { it != -1L } ?: 1L,
                        aspectos = emptyList() 
                    )
                    val response = com.grupo7poo2.digae.network.RetrofitClient.apiService.crearMatriz(request)
                    if (!response.isSuccessful) {
                        println("Error al crear matriz: ${response.code()}")
                    }
                }

                ActividadRepository.registrar(
                    titulo = if (esNueva) "Nueva matriz — ${instalacionId.trim()}" else "Matriz editada — ${instalacionId.trim()}",
                    descripcion = actividad.trim(),
                    autor = usuarioActual,
                    modulo = ModuloApp.CRITICIDAD,
                    accion = if (esNueva) TipoAccion.CREAR else TipoAccion.EDITAR
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cargarDatosDesdeRed()
                cerrarFormMatriz()
            }
        }
    }

    fun eliminarMatriz(matrizId: String) {
        val m = _matrices.find { it.id == matrizId }
        _matrices.removeAll { it.id == matrizId }
        m?.let {
            ActividadRepository.registrar(
                titulo = "Matriz eliminada — ${it.instalacionId}",
                descripcion = it.actividad,
                autor = usuarioActual,
                modulo = ModuloApp.CRITICIDAD,
                accion = TipoAccion.ELIMINAR
            )
        }
        publicarMatrices()
    }

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

        val aspectosActualizados = if (aspectoEditandoId != null) {

            matriz.aspectos.map { if (it.id == aspectoEditandoId) nuevoAspecto else it }
        } else {

            matriz.aspectos + nuevoAspecto
        }

        val matrizActualizada = MatrizAspectos(
            id = matriz.id,
            instalacionId = matriz.instalacionId,
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
            descripcion = "Matriz: ${_matrices.find { it.id == matrizId }?.instalacionId ?: matrizId}",
            autor = usuarioActual,
            modulo = ModuloApp.CRITICIDAD,
            accion = if (aspectoEditandoId == null) TipoAccion.CREAR else TipoAccion.EDITAR
        )
    }

    fun eliminarAspecto(matrizId: String, aspectoId: String) {
        val matriz = _matrices.find { it.id == matrizId } ?: return
        val asp = matriz.aspectos.find { it.id == aspectoId }
        val nuevaMatriz = MatrizAspectos(
            id = matriz.id, instalacionId = matriz.instalacionId, actividad = matriz.actividad,
            fechaRegistro = matriz.fechaRegistro, estado = matriz.estado
        )
        matriz.aspectos.filter { it.id != aspectoId }.forEach { nuevaMatriz.agregarAspecto(it) }
        val idx = _matrices.indexOfFirst { it.id == matrizId }
        if (idx >= 0) _matrices[idx] = nuevaMatriz
        asp?.let {
            ActividadRepository.registrar(
                titulo = "Aspecto eliminado — ${it.descripcion}",
                descripcion = "Matriz: ${matriz.instalacionId}",
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
        cargarDatosDesdeRed()
    }

    private fun publicarMatrices() {
        _uiState.update { it.copy(matrices = _matrices.toList(), isLoading = false) }
    }

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
