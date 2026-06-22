package com.grupo7poo2.digae.modelos

class Facultad(
    val id: String,
    val nombre: String,
    val coordinador: UsuarioOperativo
) {
    // Relación de agregación: 1 Facultad tiene varios Usuarios Operativos
    private val usuariosAsignados = mutableListOf<UsuarioOperativo>()
    private val matrices = mutableListOf<MatrizAspectos>()
    private val bitacoras = mutableListOf<BitacoraResiduos>()

    init {
        usuariosAsignados.add(coordinador)
    }

    fun agregarUsuario(usuario: UsuarioOperativo) {
        usuariosAsignados.add(usuario)
    }

    fun obtenerResiduos(): List<BitacoraResiduos> {
        return bitacoras
    }

    fun obtenerMatrices(): List<MatrizAspectos> {
        return matrices
    }
}
