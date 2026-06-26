package com.grupo7poo2.digae.network.dto

data class AuthResponseDTO(
    val token: String,
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: String,
    val facultadId: Long?
)
