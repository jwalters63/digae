package com.grupo7poo2.digae.network.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ROL = "user_rol"
        const val USER_ID = "user_id"
        const val USER_FACULTAD_ID = "user_facultad_id"
    }

    /**
     * Guarda el token JWT
     */
    fun saveAuthToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Obtiene el token JWT
     */
    fun fetchAuthToken(): String? {
        return sharedPreferences.getString(USER_TOKEN, null)
    }

    fun saveUserRole(rol: String) {
        sharedPreferences.edit().putString(USER_ROL, rol).apply()
    }

    fun fetchUserRole(): String? {
        return sharedPreferences.getString(USER_ROL, null)
    }

    fun saveUserId(id: Long) {
        sharedPreferences.edit().putLong(USER_ID, id).apply()
    }

    fun fetchUserId(): Long {
        return sharedPreferences.getLong(USER_ID, -1)
    }

    fun saveUserFacultadId(facultadId: Long) {
        sharedPreferences.edit().putLong(USER_FACULTAD_ID, facultadId).apply()
    }

    fun fetchUserFacultadId(): Long {
        return sharedPreferences.getLong(USER_FACULTAD_ID, -1)
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
