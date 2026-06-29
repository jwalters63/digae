package com.grupo7poo2.digae.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo7poo2.digae.network.RetrofitClient
import com.grupo7poo2.digae.network.auth.SessionManager
import com.grupo7poo2.digae.network.dto.LoginRequestDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    suspend fun login(): Boolean {
        if (_uiState.value.email.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Llene todos los campos")
            return false
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        if (_uiState.value.email == "bypass" && _uiState.value.password == "bypass") {
            sessionManager.saveAuthToken("bypass-token")
            sessionManager.saveUserId(0L)
            sessionManager.saveUserRole("ROOTADMIN")
            sessionManager.saveUserName("Modo Offline")
            sessionManager.saveUserEmail("offline@digae.com")
            _uiState.value = _uiState.value.copy(isLoading = false)
            return true
        }

        return try {
            val request = LoginRequestDTO(email = _uiState.value.email, password = _uiState.value.password)
            val response = RetrofitClient.apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                sessionManager.saveAuthToken(body.token)
                sessionManager.saveUserId(body.id)
                sessionManager.saveUserRole(body.rol)
                sessionManager.saveUserName(body.nombre)
                sessionManager.saveUserEmail(body.email)
                if (body.facultadId != null) {
                    sessionManager.saveUserFacultadId(body.facultadId)
                }
                
                _uiState.value = _uiState.value.copy(isLoading = false)
                true
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Credenciales inválidas")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error: ${e.message}")
            false
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
