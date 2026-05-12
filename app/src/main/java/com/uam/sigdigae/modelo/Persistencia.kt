package com.uam.sigdigae.modelo

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class BitacoraItem(
    val id: String,
    val fecha: String,
    val area: String,
    val empresa: String,
    val tipo: String,
    val peso: String
)

data class AuditoriaItem(
    val id: String,
    val fecha: String,
    val area: String,
    val supervisor: String,
    val resultados: Map<Int, String>
)

object RepositorioProyectos {
    private const val PREFS_NAME = "digae_prefs"
    private const val KEY_BITACORAS = "lista_bitacoras"
    private const val KEY_AUDITORIAS = "lista_auditorias"
    private val gson = Gson()

    fun guardarBitacora(context: Context, nueva: BitacoraItem) {
        val lista = obtenerBitacoras(context).toMutableList()
        lista.add(nueva)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_BITACORAS, gson.toJson(lista)).apply()
    }

    fun obtenerBitacoras(context: Context): List<BitacoraItem> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_BITACORAS, null) ?: return emptyList()
        return gson.fromJson(json, object : TypeToken<List<BitacoraItem>>() {}.type)
    }

    fun guardarAuditoria(context: Context, nueva: AuditoriaItem) {
        val lista = obtenerAuditorias(context).toMutableList()
        lista.add(nueva)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_AUDITORIAS, gson.toJson(lista)).apply()
    }

    fun obtenerAuditorias(context: Context): List<AuditoriaItem> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_AUDITORIAS, null) ?: return emptyList()
        return gson.fromJson(json, object : TypeToken<List<AuditoriaItem>>() {}.type)
    }
}