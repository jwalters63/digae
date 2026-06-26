package com.grupo7poo2.digae.network.dto

import com.google.gson.annotations.SerializedName

data class SupervisionRequestDTO(
    @SerializedName("fechaInspeccion")
    val fechaInspeccion: String,
    @SerializedName("areaInspeccionada")
    val areaInspeccionada: String,
    @SerializedName("calificacionGeneral")
    val calificacionGeneral: Double,
    @SerializedName("observaciones")
    val observaciones: String?,
    @SerializedName("inspectorId")
    val inspectorId: Long,
    @SerializedName("items")
    val items: List<ItemSupervisionDTO>
)
