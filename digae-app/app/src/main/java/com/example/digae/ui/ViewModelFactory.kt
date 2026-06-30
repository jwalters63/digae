package com.example.digae.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.digae.data.network.SupabaseClient
import com.example.digae.data.repository.AuthRepository
import com.example.digae.ui.auth.AuthViewModel

/**
 * Patrón Factory: Se encarga de instanciar los ViewModels inyectándoles
 * sus dependencias (como los Repositorios), asegurando que todas las pantallas
 * compartan la misma instancia del repositorio y cliente.
 */
class ViewModelFactory : ViewModelProvider.Factory {

    // Instanciamos el repositorio pasándole el cliente Singleton que creamos antes.
    private val authRepository = AuthRepository(SupabaseClient.client)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("ViewModel no conocido")
    }
}
