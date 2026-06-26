package com.grupo7poo2.digae.network.dto

import com.google.gson.annotations.SerializedName

data class ControlOperacionalDTO(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("descripcion")
    val descripcion: String
)
