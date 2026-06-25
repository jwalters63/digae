package com.grupo7poo2.digae.modelos

import java.util.UUID

enum class TipoInstalacion(val label: String) {
    FACULTAD("Facultad"),
    LABORATORIO("Laboratorio"),
    BIBLIOTECA("Biblioteca"),
    ADMINISTRACION("Administración"),
    EXTERIOR("Área Exterior")
}

data class Instalacion(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val tipo: TipoInstalacion,
    val ubicacion: String
)
