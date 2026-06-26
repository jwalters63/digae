package com.grupo7poo2.digae.network.dto

import com.google.gson.annotations.SerializedName

data class BitacoraResiduoResponseDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("fechaRegistro")
    val fechaRegistro: String,
    @SerializedName("areaGeneradora")
    val areaGeneradora: String,
    @SerializedName("empresaRecolectora")
    val empresaRecolectora: String?,
    @SerializedName("observaciones")
    val observaciones: String?,
    @SerializedName("usuarioId")
    val usuarioId: Long,
    @SerializedName("usuarioNombreCompleto")
    val usuarioNombreCompleto: String?,
    @SerializedName("creadoEn")
    val creadoEn: String?,
    @SerializedName("residuos")
    val residuos: List<ResiduoDTO>?
)
