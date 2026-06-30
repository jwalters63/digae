package com.example.digae.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    @SerialName("id")
    val id: String,
    
    @SerialName("first_name")
    val firstName: String? = null,
    
    @SerialName("last_name")
    val lastName: String? = null,
    
    @SerialName("role")
    val role: String? = null
)
