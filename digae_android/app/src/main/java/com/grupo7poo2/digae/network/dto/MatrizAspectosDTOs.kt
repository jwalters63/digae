package com.grupo7poo2.digae.network.dto

import com.google.gson.annotations.SerializedName

data class MatrizAspectosRequestDTO(
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("fechaEvaluacion")
    val fechaEvaluacion: String?,
    @SerializedName("facultadId")
    val facultadId: Long,
    @SerializedName("creadoPorId")
    val creadoPorId: Long,
    @SerializedName("estado")
    val estado: String?,
    @SerializedName("aspectos")
    val aspectos: List<AspectAmbientalRequestDTO>
)

data class MatrizAspectosResponseDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("fechaEvaluacion")
    val fechaEvaluacion: String?,
    @SerializedName("facultadId")
    val facultadId: Long,
    @SerializedName("facultadNombre")
    val facultadNombre: String?,
    @SerializedName("creadoPorId")
    val creadoPorId: Long,
    @SerializedName("creadoPorNombreCompleto")
    val creadoPorNombreCompleto: String?,
    @SerializedName("estado")
    val estado: String?,
    @SerializedName("aspectos")
    val aspectos: List<AspectAmbientalResponseDTO>?
)
