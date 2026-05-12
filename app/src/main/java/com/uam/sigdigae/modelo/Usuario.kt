package com.uam.sigdigae.modelo

open class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: String
)

class Administrador(id: String, nombre: String, correo: String) :
    Usuario(id, nombre, correo, "ADMINISTRADOR")

class UsuarioOperativo(id: String, nombre: String, correo: String, val facultad: String) :
    Usuario(id, nombre, correo, "OPERATIVO")