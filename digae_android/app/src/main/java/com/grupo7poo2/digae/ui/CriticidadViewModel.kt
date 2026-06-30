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
    private val usuarioActual = "Usuario Actual"

    init {
        cargarDatosDesdeRed()
    }

    fun cargarDatosDesdeRed() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val response = com.grupo7poo2.digae.network.RetrofitClient.apiService.obtenerMatrices()
                if (response.isSuccessful) {
                    val dtos = response.body() ?: emptyList()
                    val matricesParsed = dtos.map { dto ->
                        val df = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        val fechaParsed = try {
                            df.parse(dto.fechaEvaluacion ?: "") ?: Date()
                        } catch (e: Exception) { Date() }

                        val estadoParseado = try {
                            if (dto.estado != null) EstadoMatriz.valueOf(dto.estado) else EstadoMatriz.BORRADOR
                        } catch (e: Exception) { EstadoMatriz.BORRADOR }

                        val m = MatrizAspectos(
                            id = dto.id.toString(),
                            instalacionId = dto.facultadId.toString(),
                            actividad = dto.nombre,
                            fechaRegistro = fechaParsed,
                            estado = estadoParseado
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
                    }
                    _uiState.update { it.copy(matrices = matricesParsed, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Error al obtener matrices: ${response.code()}", isLoading = false) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(error = "Falla de red al obtener matrices: ${e.message}", isLoading = false) }
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
                _uiState.update { it.copy(isLoading = true, error = null) }
                val request = com.grupo7poo2.digae.network.dto.MatrizAspectosRequestDTO(
                    nombre = actividad,
                    fechaEvaluacion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date()),
                    facultadId = instalacionId.toLongOrNull() ?: (sessionManager.fetchUserFacultadId().takeIf { it != -1L } ?: 1L),
                    creadoPorId = sessionManager.fetchUserId().takeIf { it != -1L } ?: 1L,
                    estado = estado.name,
                    aspectos = emptyList() 
                )

                if (esNueva) {
                    val response = com.grupo7poo2.digae.network.RetrofitClient.apiService.crearMatriz(request)
                    if (!response.isSuccessful) {
                        _uiState.update { it.copy(error = "Error del servidor al crear: ${response.code()}", isLoading = false) }
                        return@launch
                    }
                } else {
                    val idLong = idExistente.toLongOrNull()
                    if (idLong != null) {
                        val response = com.grupo7poo2.digae.network.RetrofitClient.apiService.actualizarMatriz(idLong, request)
                        if (!response.isSuccessful) {
                            _uiState.update { it.copy(error = "Error del servidor al editar: ${response.code()}", isLoading = false) }
                            return@launch
                        }
                    }
                }

                ActividadRepository.registrar(
                    titulo = if (esNueva) "Nueva matriz — ${instalacionId.trim()}" else "Matriz editada — ${instalacionId.trim()}",
                    descripcion = actividad.trim(),
                    autor = usuarioActual,
                    modulo = ModuloApp.CRITICIDAD,
                    accion = if (esNueva) TipoAccion.CREAR else TipoAccion.EDITAR
                )
                
                cargarDatosDesdeRed()
                cerrarFormMatriz()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(error = "Falla de red: ${e.message}", isLoading = false) }
            }
        }
    }

    fun eliminarMatriz(matrizId: String) {
        val m = _uiState.value.matrices.find { it.id == matrizId } ?: return
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val idLong = matrizId.toLongOrNull()
                if (idLong != null) {
                    val response = com.grupo7poo2.digae.network.RetrofitClient.apiService.eliminarMatriz(idLong)
                    if (!response.isSuccessful) {
                        _uiState.update { it.copy(error = "No se pudo eliminar en el servidor: ${response.code()}", isLoading = false) }
                        return@launch
                    }
                }
                
                _uiState.update { state -> 
                    state.copy(
                        matrices = state.matrices.filterNot { it.id == matrizId },
                        isLoading = false
                    ) 
                }
                
                ActividadRepository.registrar(
                    titulo = "Matriz eliminada — ${m.instalacionId}",
                    descripcion = m.actividad,
                    autor = usuarioActual,
                    modulo = ModuloApp.CRITICIDAD,
                    accion = TipoAccion.ELIMINAR
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(error = "Falla de red al eliminar: ${e.message}", isLoading = false) }
            }
        }
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
        val matricesActuales = _uiState.value.matrices
        val matriz = matricesActuales.find { it.id == matrizId } ?: return
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

        _uiState.update { state ->
            state.copy(
                matrices = state.matrices.map { if (it.id == matrizId) matrizActualizada else it }
            )
        }

        cerrarFormAspecto()
        ActividadRepository.registrar(
            titulo = if (aspectoEditandoId == null) "Aspecto registrado — ${descripcion.trim()}" else "Aspecto editado — ${descripcion.trim()}",
            descripcion = "Matriz: ${matriz.instalacionId}",
            autor = usuarioActual,
            modulo = ModuloApp.CRITICIDAD,
            accion = if (aspectoEditandoId == null) TipoAccion.CREAR else TipoAccion.EDITAR
        )
    }

    fun eliminarAspecto(matrizId: String, aspectoId: String) {
        val matricesActuales = _uiState.value.matrices
        val matriz = matricesActuales.find { it.id == matrizId } ?: return
        val asp = matriz.aspectos.find { it.id == aspectoId }
        val nuevaMatriz = MatrizAspectos(
            id = matriz.id, instalacionId = matriz.instalacionId, actividad = matriz.actividad,
            fechaRegistro = matriz.fechaRegistro, estado = matriz.estado
        )
        matriz.aspectos.filter { it.id != aspectoId }.forEach { nuevaMatriz.agregarAspecto(it) }
        
        _uiState.update { state ->
            state.copy(
                matrices = state.matrices.map { if (it.id == matrizId) nuevaMatriz else it }
            )
        }
        
        asp?.let {
            ActividadRepository.registrar(
                titulo = "Aspecto eliminado — ${it.descripcion}",
                descripcion = "Matriz: ${matriz.instalacionId}",
                autor = usuarioActual,
                modulo = ModuloApp.CRITICIDAD,
                accion = TipoAccion.ELIMINAR
            )
        }
    }

    fun obtenerMatriz(matrizId: String): MatrizAspectos? = _uiState.value.matrices.find { it.id == matrizId }

    fun obtenerAspecto(matrizId: String, aspectoId: String): AspectAmbiental? =
        _uiState.value.matrices.find { it.id == matrizId }?.aspectos?.find { it.id == aspectoId }

    fun cambiarEstrategia(nueva: CalculoCriticidadStrategy) {
        estrategia = nueva
        cargarDatosDesdeRed()
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
