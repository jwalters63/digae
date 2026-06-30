package com.example.digae.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.digae.data.local.DigaeDatabase

class AuthRepository(
    private val supabaseClient: SupabaseClient,
    private val database: DigaeDatabase
) {

    // Retorna el usuario actual si la sesión está activa
    fun currentUser(): UserInfo? {
        return supabaseClient.auth.currentUserOrNull()
    }

    // Inicia sesión usando correo y contraseña
    suspend fun login(email: String, pass: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.auth.signInWith(Email) {
                    this.email = email
                    this.password = pass
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Cierra sesión
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.auth.signOut()
                database.clearAllTables()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
