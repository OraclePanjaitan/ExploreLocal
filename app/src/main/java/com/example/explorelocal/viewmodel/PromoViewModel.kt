package com.example.explorelocal.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.explorelocal.data.model.Promo
import com.example.explorelocal.data.repository.PromoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.explorelocal.data.repository.UmkmRepository

/* -------------------- PROMO STATE -------------------- */

sealed class PromoState {
    object Idle : PromoState()
    object Loading : PromoState()

    data class Success(val message: String) : PromoState()
    data class Error(val message: String) : PromoState()

    data class PromoList(val data: List<Promo>) : PromoState()
}

/* -------------------- VIEWMODEL -------------------- */

class PromoViewModel : ViewModel() {

    private val umkmRepository = UmkmRepository()

    private val _umkmNama = MutableStateFlow<String?>(null)
    val umkmNama: StateFlow<String?> = _umkmNama

    private val repository = PromoRepository()

    // 🔹 STATE PROMO
    private val _promoState =
        MutableStateFlow<PromoState>(PromoState.Idle)
    val promoState: StateFlow<PromoState> = _promoState

    // 🔹 STATE UPLOAD BANNER (BENAR)
    private val _bannerState =
        MutableStateFlow<BannerUploadState>(BannerUploadState.Idle)
    val bannerState: StateFlow<BannerUploadState> = _bannerState

    /* -------------------- 1. UPLOAD BANNER -------------------- */

    fun uploadBanner(uri: Uri, context: Context) {
        viewModelScope.launch {
            _bannerState.value = BannerUploadState.Uploading

            val result = repository.uploadBanner(uri, context)

            if (result.isSuccess) {
                _bannerState.value =
                    BannerUploadState.Success(result.getOrNull()!!)
            } else {
                _bannerState.value =
                    BannerUploadState.Error(
                        result.exceptionOrNull()?.message
                            ?: "Gagal mengupload banner"
                    )
            }
        }
    }

    fun insertPromo(
        umkmId: String,
        judul: String,
        deskripsi: String,
        tanggalMulai: String,
        tanggalSelesai: String,
        bannerUrl: String,
        jenisPromo: String
    ) {
        viewModelScope.launch {
            _promoState.value = PromoState.Loading

            val promo = Promo(
                umkmId = umkmId,
                judul = judul,
                deskripsi = deskripsi,
                tanggalMulai = tanggalMulai,
                tanggalSelesai = tanggalSelesai,
                bannerUrl = bannerUrl,
                jenisPromo = jenisPromo
            )

            val result = repository.insertPromo(promo)

            _promoState.value = if (result.isSuccess) {
                PromoState.Success("Promo berhasil ditambahkan 🎉")
            } else {
                PromoState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menyimpan promo"
                )
            }
        }
    }

    /* -------------------- LOAD DATA -------------------- */

    fun loadAllPromo() {
        viewModelScope.launch {
            _promoState.value = PromoState.Loading
            val result = repository.getAllPromo()

            if (result.isSuccess) {
                _promoState.value =
                    PromoState.PromoList(result.getOrNull() ?: emptyList())
            } else {
                _promoState.value =
                    PromoState.Error("Gagal memuat promo")
            }
        }
    }

    fun loadPromoByUmkm(umkmId: String) {
        viewModelScope.launch {
            _promoState.value = PromoState.Loading
            val result = repository.getPromoByUmkm(umkmId)

            if (result.isSuccess) {
                _promoState.value =
                    PromoState.PromoList(result.getOrNull() ?: emptyList())
            } else {
                _promoState.value =
                    PromoState.Error("Gagal memuat promo UMKM")
            }
        }
    }

    /* -------------------- UPDATE & DELETE -------------------- */

    fun updatePromo(id: String, promo: Promo) {
        viewModelScope.launch {
            _promoState.value = PromoState.Loading

            val result = repository.updatePromo(id, promo)
            if (result.isSuccess) {
                _promoState.value =
                    PromoState.Success("Promo berhasil diperbarui")
                loadAllPromo()
            } else {
                _promoState.value =
                    PromoState.Error("Gagal memperbarui promo")
            }
        }
    }

    fun deletePromo(id: String) {
        viewModelScope.launch {
            _promoState.value = PromoState.Loading

            val result = repository.deletePromo(id)
            if (result.isSuccess) {
                _promoState.value =
                    PromoState.Success("Promo berhasil dihapus")
                loadAllPromo()
            } else {
                _promoState.value =
                    PromoState.Error("Gagal menghapus promo")
            }
        }
    }

    fun loadUmkmName(umkmId: String) {
        viewModelScope.launch {
            val result = umkmRepository.getUmkmById(umkmId)
            if (result.isSuccess) {
                _umkmNama.value = result.getOrNull()?.nama
            }
        }
    }

    /* -------------------- RESET -------------------- */

    fun resetPromoState() {
        _promoState.value = PromoState.Idle
    }

    fun resetBannerState() {
        _bannerState.value = BannerUploadState.Idle
    }
}
