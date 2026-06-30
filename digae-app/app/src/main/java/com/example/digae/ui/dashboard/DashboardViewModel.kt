package com.example.digae.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardState(
    val userName: String = "Juan Pérez",
    val userInitials: String = "JP",
    val userRole: String = "ADMINISTRADOR",
    val matricesCount: Int = 12,
    val auditoriasCount: Int = 3,
    val bitacorasCount: Int = 45
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    // Aquí añadiremos la lógica para obtener los datos de Supabase más adelante
}
