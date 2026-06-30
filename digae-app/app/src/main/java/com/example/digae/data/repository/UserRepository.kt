package com.example.digae.data.repository

import com.example.digae.data.local.dao.UserProfileDao
import com.example.digae.data.local.entity.UserProfileEntity
import com.example.digae.data.model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(
    private val supabaseClient: SupabaseClient,
    private val userProfileDao: UserProfileDao
) {

    // Devuelve un Flow que observa la base de datos local
    fun getUserProfileStream(userId: String): Flow<UserProfileEntity?> {
        return userProfileDao.getUserProfile(userId)
    }

    // Intenta refrescar los datos desde Supabase
    suspend fun refreshUserProfile(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Obtener el perfil remoto
                val remoteProfile = supabaseClient.postgrest["profiles"]
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingle<UserProfile>()

                // Actualizar la caché local
                val entity = UserProfileEntity(
                    id = remoteProfile.id,
                    firstName = remoteProfile.firstName ?: "Usuario",
                    lastName = remoteProfile.lastName ?: "",
                    role = remoteProfile.role ?: "OPERATIVO"
                )
                userProfileDao.insertProfile(entity)

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
