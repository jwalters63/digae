package com.grupo7poo2.digae.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // 10.0.2.2 es el alias que usa el emulador de Android para acceder a localhost (127.0.0.1) de tu computadora.
    // Si pruebas en un dispositivo físico, cambia esto por la IP local de tu PC (ej: "http://192.168.1.5:8081/")
    private const val BASE_URL = "http://10.0.2.2:8081/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private lateinit var okHttpClient: OkHttpClient

    fun initialize(context: android.content.Context) {
        val sessionManager = com.grupo7poo2.digae.network.auth.SessionManager(context)
        val authInterceptor = com.grupo7poo2.digae.network.auth.AuthInterceptor(sessionManager)

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: ApiService by lazy {
        if (!::okHttpClient.isInitialized) {
            throw IllegalStateException("RetrofitClient must be initialized with Context first.")
        }
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
