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

sealed class PromoState {
    object Idle : PromoState()
    object Loading : PromoState()
    object Uploading : PromoState()

    data class Success(val message: String) : PromoState()
    data class Error(val message: String) : PromoState()

    data class PromoList(val data: List<Promo>) : PromoState()
    data class PromoDetail(val data: Promo) : PromoState()
}

// ---------------- VIEWMODEL ----------------
class PromoViewModel : ViewModel() {

    private val repository = PromoRepository()

    private val _promoState = MutableStateFlow<PromoState>(PromoState.Idle)
    val promoState: StateFlow<PromoState> = _promoState

    private val _uploadedBannerUrl = MutableStateFlow<String?>(null)
    val uploadedBannerUrl: StateFlow<String?> = _uploadedBannerUrl


    // -------------------- 1. Upload Banner --------------------
    fun uploadBanner(uri: Uri, context: Context) {
        viewModelScope.launch {
            _promoState.value = PromoState.Uploading

            val result = repository.uploadBanner(uri, context)

            if (result.isSuccess) {
                _uploadedBannerUrl.value = result.getOrNull()
                _promoState.value = PromoState.Idle
            } else {
                _promoState.value = PromoState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal mengupload banner"
                )
            }
        }
    }


    // -------------------- 2. Insert Promo --------------------
    fun insertPromo(
        umkmId: String,
        judul: String,
        deskripsi: String?,
        tanggalMulai: String?,
        tanggalSelesai: String?,
        bannerUrl: String?,
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

            if (result.isSuccess) {
                _promoState.value = PromoState.Success("Promo berhasil ditambahkan")
                loadAllPromo() // refresh list
            } else {
                _promoState.value = PromoState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menambahkan promo"
                )
            }
        }
    }


    // -------------------- 3. Get Semua Promo --------------------
    fun loadAllPromo() {
        viewModelScope.launch {
            _promoState.value = PromoState.Loading

            val result = repository.getAllPromo()
            if (result.isSuccess) {
                _promoState.value = PromoState.PromoList(result.getOrNull() ?: emptyList())
            } else {
                _promoState.value = PromoState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal memuat promo"
                )
            }
        }
    }


    // -------------------- 4. Get Promo by UMKM --------------------
    fun loadPromoByUmkm(umkmId: String) {
        viewModelScope.launch {
            _promoState.value = PromoState.Loading

            val result = repository.getPromoByUmkm(umkmId)
            if (result.isSuccess) {
                _promoState.value = PromoState.PromoList(result.getOrNull() ?: emptyList())
            } else {
                _promoState.value = PromoState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal memuat promo UMKM"
                )
            }
        }
    }


    // -------------------- 5. Update Promo --------------------
    fun updatePromo(id: String, promo: Promo) {
        viewModelScope.launch {
            _promoState.value = PromoState.Loading

            val result = repository.updatePromo(id, promo)

            if (result.isSuccess) {
                _promoState.value = PromoState.Success("Promo berhasil diperbarui")
                loadAllPromo()
            } else {
                _promoState.value = PromoState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal memperbarui promo"
                )
            }
        }
    }


    // -------------------- 6. Delete Promo --------------------
    fun deletePromo(id: String) {
        viewModelScope.launch {
            _promoState.value = PromoState.Loading

            val result = repository.deletePromo(id)

            if (result.isSuccess) {
                _promoState.value = PromoState.Success("Promo berhasil dihapus")
                loadAllPromo()
            } else {
                _promoState.value = PromoState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menghapus promo"
                )
            }
        }
    }


    // -------------------- 7. Reset State --------------------
    fun resetState() {
        _promoState.value = PromoState.Idle
    }

    fun resetUploadedBanner() {
        _uploadedBannerUrl.value = null
    }
}
