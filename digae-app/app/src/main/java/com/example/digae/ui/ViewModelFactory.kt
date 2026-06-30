package com.example.digae.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.digae.data.local.DigaeDatabase
import com.example.digae.data.network.SupabaseClient
import com.example.digae.data.repository.AuthRepository
import com.example.digae.data.repository.UserRepository
import com.example.digae.ui.auth.AuthViewModel
import com.example.digae.ui.dashboard.DashboardViewModel

/**
 * Patrón Factory: Se encarga de instanciar los ViewModels inyectándoles
 * sus dependencias (como los Repositorios), asegurando que todas las pantallas
 * compartan la misma instancia del repositorio y cliente.
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val database by lazy { DigaeDatabase.getDatabase(context) }
    private val authRepository by lazy { AuthRepository(SupabaseClient.client, database) }
    private val userRepository by lazy { UserRepository(SupabaseClient.client, database.userProfileDao()) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(authRepository, userRepository) as T
        }
        throw IllegalArgumentException("ViewModel no conocido")
    }
}
