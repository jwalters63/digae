package com.grupo7poo2.digae.network.dto

import com.google.gson.annotations.SerializedName

data class SupervisionResponseDTO(
    @SerializedName("id")
    val id: Long,
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
    @SerializedName("inspectorNombreCompleto")
    val inspectorNombreCompleto: String?,
    @SerializedName("creadoEn")
    val creadoEn: String?,
    @SerializedName("items")
    val items: List<ItemSupervisionDTO>?
)
