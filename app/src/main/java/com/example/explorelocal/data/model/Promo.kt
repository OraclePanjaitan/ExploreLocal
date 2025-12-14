package com.example.explorelocal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Promo(

    @SerialName("id")
    val id: String? = null,

    @SerialName("umkm_id")
    val umkmId: String,

    @SerialName("judul")
    val judul: String,

    @SerialName("deskripsi")
    val deskripsi: String? = null,

    @SerialName("tanggal_mulai")
    val tanggalMulai: String? = null,

    @SerialName("tanggal_selesai")
    val tanggalSelesai: String? = null,

    @SerialName("banner_url")
    val bannerUrl: String? = null,

    @SerialName("jenis_promo")
    val jenisPromo: String,

    @SerialName("created_at")
    val createdAt: String? = null
)
