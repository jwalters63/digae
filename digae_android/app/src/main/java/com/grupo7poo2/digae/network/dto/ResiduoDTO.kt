package com.grupo7poo2.digae.network.dto

import com.google.gson.annotations.SerializedName

data class ResiduoDTO(
    @SerializedName("clasificacion")
    val clasificacion: String,
    @SerializedName("pesoKg")
    val pesoKg: Double,
    @SerializedName("volumenM3")
    val volumenM3: Double? = null
)
