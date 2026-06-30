package com.example.digae.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String,
    val nombre: String,
    val rol: String
)

@Serializable
data class Facultad(
    val id: String,
    val nombre: String,
    @SerialName("coordinador_id")
    val coordinadorId: String? = null
)

@Serializable
data class MatrizAspectos(
    val id: String,
    @SerialName("facultad_id")
    val facultadId: String,
    val actividad: String,
    @SerialName("fecha_registro")
    val fechaRegistro: String
)

@Serializable
data class AspectoAmbiental(
    val id: String,
    @SerialName("matriz_id")
    val matrizId: String,
    val descripcion: String,
    val gravedad: Int,
    val severidad: Int,
    val probabilidad: Int,
    val criticidad: Int
)

@Serializable
data class ControlOperacional(
    val id: String,
    @SerialName("aspecto_id")
    val aspectoId: String,
    val descripcion: String,
    @SerialName("tipo_control")
    val tipoControl: String
)

@Serializable
data class Supervision(
    val id: String,
    val fecha: String,
    @SerialName("supervisor_id")
    val supervisorId: String,
    @SerialName("facultad_id")
    val facultadId: String,
    val estado: String
)

@Serializable
data class ItemSupervision(
    val id: String,
    @SerialName("supervision_id")
    val supervisionId: String,
    val categoria: String,
    val descripcion: String,
    val resultado: String
)

@Serializable
data class BitacoraResiduos(
    val id: String,
    val fecha: String,
    @SerialName("facultad_id")
    val facultadId: String,
    @SerialName("empresa_recolectora")
    val empresaRecolectora: String
)

@Serializable
data class Residuo(
    val id: String,
    @SerialName("bitacora_id")
    val bitacoraId: String,
    val tipo: String,
    val peso: Double? = null,
    val volumen: Double? = null
)
