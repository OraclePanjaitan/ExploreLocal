package com.example.explorelocal.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRole(
    val id: String? = null,
    val user_id: String,
    val role: String,
    val created_at: String? = null
)
