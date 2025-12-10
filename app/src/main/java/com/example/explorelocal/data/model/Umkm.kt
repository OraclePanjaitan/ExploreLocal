package com.example.explorelocal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Umkm(
    @SerialName("id")
    val id: String? = null,

    @SerialName("nama")
    val nama: String,

    @SerialName("kategori")
    val kategori: String? = null,

    @SerialName("deskripsi")
    val deskripsi: String? = null,

    @SerialName("foto_url")
    val fotoUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null, // nullable agar auto-generate

    @SerialName("updated_at")
    val updatedAt: String? = null // nullable agar auto-generate
)
