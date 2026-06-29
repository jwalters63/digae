package com.grupo7poo2.digae.network

import com.grupo7poo2.digae.network.dto.BitacoraResiduoRequestDTO
import com.grupo7poo2.digae.network.dto.BitacoraResiduoResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("api/v1/bitacora-residuos")
    suspend fun obtenerRegistrosBitacora(): Response<List<BitacoraResiduoResponseDTO>>

    @POST("api/v1/bitacora-residuos")
    suspend fun crearRegistroBitacora(@Body request: BitacoraResiduoRequestDTO): Response<BitacoraResiduoResponseDTO>

    @GET("api/v1/supervisiones")
    suspend fun obtenerSupervisiones(): Response<List<com.grupo7poo2.digae.network.dto.SupervisionResponseDTO>>

    @POST("api/v1/supervisiones")
    suspend fun crearSupervision(@Body request: com.grupo7poo2.digae.network.dto.SupervisionRequestDTO): Response<com.grupo7poo2.digae.network.dto.SupervisionResponseDTO>

    @GET("api/v1/matrices")
    suspend fun obtenerMatrices(): Response<List<com.grupo7poo2.digae.network.dto.MatrizAspectosResponseDTO>>

    @POST("api/v1/matrices")
    suspend fun crearMatriz(@Body request: com.grupo7poo2.digae.network.dto.MatrizAspectosRequestDTO): Response<com.grupo7poo2.digae.network.dto.MatrizAspectosResponseDTO>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: com.grupo7poo2.digae.network.dto.LoginRequestDTO): Response<com.grupo7poo2.digae.network.dto.AuthResponseDTO>

    @GET("api/instalaciones")
    suspend fun obtenerInstalaciones(): Response<List<com.grupo7poo2.digae.modelos.Instalacion>>

    @POST("api/instalaciones")
    suspend fun crearInstalacion(@Body instalacion: com.grupo7poo2.digae.modelos.Instalacion): Response<com.grupo7poo2.digae.modelos.Instalacion>

    @retrofit2.http.DELETE("api/instalaciones/{id}")
    suspend fun eliminarInstalacion(@retrofit2.http.Path("id") id: String): Response<Void>
}
