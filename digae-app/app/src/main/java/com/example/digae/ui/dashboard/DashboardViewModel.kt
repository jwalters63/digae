package com.example.digae.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digae.data.repository.AuthRepository
import com.example.digae.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardState(
    val userName: String = "Cargando...",
    val userEmail: String = "cargando@uam.edu.ni",
    val userInitials: String = "",
    val userRole: String = "Cargando...",
    val matricesCount: Int = 12,
    val auditoriasCount: Int = 3,
    val bitacorasCount: Int = 45
)

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = authRepository.currentUser()
        if (user != null) {
            val userId = user.id
            val email = user.email ?: ""

            // Escuchamos los cambios desde Room
            viewModelScope.launch {
                userRepository.getUserProfileStream(userId).collect { profile ->
                    if (profile != null) {
                        val fullName = "${profile.firstName} ${profile.lastName}".trim()
                        val initials = "${profile.firstName.firstOrNull() ?: ""}${profile.lastName.firstOrNull() ?: ""}".uppercase()

                        _uiState.update { current ->
                            current.copy(
                                userName = fullName,
                                userEmail = email,
                                userInitials = initials,
                                userRole = profile.role
                            )
                        }
                    }
                }
            }

            // Solicitamos a Supabase que actualice la caché de Room
            viewModelScope.launch {
                userRepository.refreshUserProfile(userId)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
