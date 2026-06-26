package com.grupo7poo2.digae.network.dto

import com.google.gson.annotations.SerializedName

data class AspectAmbientalRequestDTO(
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("gravedad")
    val gravedad: Int,
    @SerializedName("severidad")
    val severidad: Int,
    @SerializedName("probabilidad")
    val probabilidad: Int,
    @SerializedName("controles")
    val controles: List<ControlOperacionalDTO>
)

data class AspectAmbientalResponseDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("gravedad")
    val gravedad: Int,
    @SerializedName("severidad")
    val severidad: Int,
    @SerializedName("probabilidad")
    val probabilidad: Int,
    @SerializedName("nivelCriticidad")
    val nivelCriticidad: String?,
    @SerializedName("controles")
    val controles: List<ControlOperacionalDTO>?
)
