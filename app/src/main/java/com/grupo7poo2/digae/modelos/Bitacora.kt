package com.grupo7poo2.digae.modelos

import java.util.Date

class BitacoraResiduos(
    val id: String,
    val fecha: Date,
    val area: String,
    val empresa: String,
    val tipo: String
) {
    private val residuos = mutableListOf<Residuo>()
    var firma: FirmaDigital? = null
        private set

    fun registrarSalida(residuo: Residuo) {
        residuos.add(residuo)
    }

    fun capturarFirma(nuevaFirma: FirmaDigital) {
        firma = nuevaFirma
    }

    fun obtenerHistorial(): List<Residuo> = residuos.toList()
}

class Residuo(
    val id: String,
    val tipo: TipoResiduo,
    var peso: Double,
    var volumen: Double
) {
    fun clasificar(nuevoTipo: TipoResiduo) {
        // Modificar clasificación
    }

    fun calcularVolumen(): Double {
        return volumen * peso // Ejemplo de cálculo
    }
}

enum class TipoResiduo {
    COMUN, PELIGROSO, BIOINFECCIOSO
}

class FirmaDigital(
    val id: String,
    val datosCanvas: ByteArray, // Representación de la firma
    val fechaCaptura: Date,
    val firmante: String
) {
    fun capturar() {
        // Iniciar captura Canvas
    }

    fun validar(): Boolean {
        return datosCanvas.isNotEmpty()
    }

    fun guardar() {
        // Persistencia
    }
}
