package com.grupo7poo2.digae.ui

import androidx.lifecycle.ViewModel
import com.grupo7poo2.digae.modelos.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

// ─── Estado de la UI ─────────────────────────────────────────────────────────
data class CriticidadUiState(
    val matrices: List<MatrizAspectos> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ─── ViewModel ───────────────────────────────────────────────────────────────
class CriticidadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CriticidadUiState(isLoading = true))
    val uiState: StateFlow<CriticidadUiState> = _uiState.asStateFlow()

    // Estrategia de cálculo intercambiable (patrón Strategy)
    private var estrategia: CalculoCriticidadStrategy = MotorCalculoMultiplicativo()

    init {
        cargarDatosSemilla()
    }

    /** Carga datos de demostración coherentes con los módulos del sistema */
    private fun cargarDatosSemilla() {
        val matrices = listOf(
            crearMatrizIngenieria(),
            crearMatrizMedicina(),
            crearMatrizAdministrativo()
        )
        _uiState.value = CriticidadUiState(matrices = matrices, isLoading = false)
    }

    private fun crearMatrizIngenieria(): MatrizAspectos {
        val matriz = MatrizAspectos(
            id = "MAT-001",
            area = "Facultad de Ingeniería",
            actividad = "Laboratorios de química y manufactura",
            fechaRegistro = Date(),
            estado = EstadoMatriz.APROBADA
        )
        matriz.agregarAspecto(AspectAmbiental(
            id = "ASP-001", descripcion = "Emisión de vapores químicos",
            descripcionImpacto = "Contaminación del aire interior y exterior",
            tipoAspecto = TipoAspecto.AIRE,
            gravedad = 4, severidad = 5, probabilidad = 3, calculador = estrategia
        ).also { it.asignarControl(ControlOperacional("CT-001", "Extractor de aire forzado", 50.0, "Ingeniería")) })

        matriz.agregarAspecto(AspectAmbiental(
            id = "ASP-002", descripcion = "Vertido de residuos líquidos al drenaje",
            descripcionImpacto = "Contaminación hídrica",
            tipoAspecto = TipoAspecto.AGUA,
            gravedad = 5, severidad = 5, probabilidad = 2, calculador = estrategia
        ).also { it.asignarControl(ControlOperacional("CT-002", "Trampa de grasas y neutralizador", 40.0, "Ingeniería")) })

        matriz.agregarAspecto(AspectAmbiental(
            id = "ASP-003", descripcion = "Consumo energético de equipos en stand-by",
            descripcionImpacto = "Aumento de huella de carbono",
            tipoAspecto = TipoAspecto.ENERGIA,
            gravedad = 2, severidad = 3, probabilidad = 5, calculador = estrategia
        ))
        return matriz
    }

    private fun crearMatrizMedicina(): MatrizAspectos {
        val matriz = MatrizAspectos(
            id = "MAT-002",
            area = "Facultad de Medicina",
            actividad = "Prácticas clínicas y manejo de biológicos",
            fechaRegistro = Date(),
            estado = EstadoMatriz.EN_REVISION
        )
        matriz.agregarAspecto(AspectAmbiental(
            id = "ASP-004", descripcion = "Generación de residuos bioinfecciosos",
            descripcionImpacto = "Riesgo sanitario y contaminación de suelo",
            tipoAspecto = TipoAspecto.RESIDUOS,
            gravedad = 5, severidad = 5, probabilidad = 4, calculador = estrategia
        ).also { it.asignarControl(ControlOperacional("CT-003", "Recolección diferenciada y autoclave", 80.0, "Administrativa")) })

        matriz.agregarAspecto(AspectAmbiental(
            id = "ASP-005", descripcion = "Uso de formaldehído en conservación",
            descripcionImpacto = "Afectación a la salud por exposición crónica",
            tipoAspecto = TipoAspecto.AIRE,
            gravedad = 5, severidad = 4, probabilidad = 3, calculador = estrategia
        ))
        return matriz
    }

    private fun crearMatrizAdministrativo(): MatrizAspectos {
        val matriz = MatrizAspectos(
            id = "MAT-003",
            area = "Edificio Administrativo",
            actividad = "Operación general de oficinas",
            fechaRegistro = Date(),
            estado = EstadoMatriz.BORRADOR
        )
        matriz.agregarAspecto(AspectAmbiental(
            id = "ASP-006", descripcion = "Consumo de papel y plásticos",
            descripcionImpacto = "Generación de residuos sólidos",
            tipoAspecto = TipoAspecto.RESIDUOS,
            gravedad = 2, severidad = 2, probabilidad = 5, calculador = estrategia
        ))
        matriz.agregarAspecto(AspectAmbiental(
            id = "ASP-007", descripcion = "Consumo de agua en sanitarios",
            descripcionImpacto = "Presión sobre el sistema hídrico institucional",
            tipoAspecto = TipoAspecto.AGUA,
            gravedad = 2, severidad = 3, probabilidad = 4, calculador = estrategia
        ))
        return matriz
    }

    fun cambiarEstrategia(nueva: CalculoCriticidadStrategy) {
        estrategia = nueva
        cargarDatosSemilla() // Recalcular con nueva estrategia
    }
}
