package com.grupo7poo2.digae.modelos

abstract class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    protected var contrasena: String
) {
    abstract val rol: String

    fun autenticar(pass: String): Boolean {
        return contrasena == pass
    }

    abstract fun obtenerPerfil(): String

    fun cerrarSesion() {

    }
}

class Administrador(
    id: String, nombre: String, correo: String, contrasena: String,
    val nivelAcceso: Int,
    val areaCargo: String
) : Usuario(id, nombre, correo, contrasena) {
    override val rol: String = "ADMINISTRADOR"

    override fun obtenerPerfil(): String = "Administrador - $areaCargo"

    fun gestionarMatriz() {  }
    fun generarReporte() {  }
    fun configurarSistema() {  }
}

class UsuarioOperativo(
    id: String, nombre: String, correo: String, contrasena: String,
    val facultad: String,
    val areaAsignada: String
) : Usuario(id, nombre, correo, contrasena) {
    override val rol: String = "OPERATIVO"

    override fun obtenerPerfil(): String = "Operativo - $facultad"

    fun registrarResiduo() {  }
    fun consultarMatriz() {  }
}

object UsuarioFactory {
    fun crearUsuario(
        tipo: String, id: String, nombre: String, correo: String, contrasena: String,
        nivelAcceso: Int = 1, areaCargo: String = "", facultad: String = "", areaAsignada: String = ""
    ): Usuario {
        return when (tipo.uppercase()) {
            "ADMINISTRADOR" -> Administrador(id, nombre, correo, contrasena, nivelAcceso, areaCargo)
            "OPERATIVO" -> UsuarioOperativo(id, nombre, correo, contrasena, facultad, areaAsignada)
            else -> throw IllegalArgumentException("Tipo de usuario no válido")
        }
    }
}