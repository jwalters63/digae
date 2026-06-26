package com.grupo7poo2.digae.network.dto

import com.google.gson.annotations.SerializedName

data class ItemSupervisionDTO(
    @SerializedName("categoria")
    val categoria: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("resultado")
    val resultado: String // ENUM string (CONFORME, NO_CONFORME, NO_APLICA)
)
