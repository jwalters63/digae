package com.grupo7poo2.digae.network.dto

import com.google.gson.annotations.SerializedName

data class BitacoraResiduoRequestDTO(
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
    @SerializedName("residuos")
    val residuos: List<ResiduoDTO>
)
