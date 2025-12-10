package com.example.explorelocal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.explorelocal.data.model.Umkm
import com.example.explorelocal.data.repository.UmkmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UmkmState {
    object Idle : UmkmState()
    object Loading : UmkmState()
    data class Success(val message: String) : UmkmState()
    data class Error(val message: String) : UmkmState()
    data class UmkmList(val data: List<Umkm>) : UmkmState()
}

class UmkmViewModel : ViewModel() {
    private val repository = UmkmRepository()

    private val _umkmState = MutableStateFlow<UmkmState>(UmkmState.Idle)
    val umkmState: StateFlow<UmkmState> = _umkmState

    fun insertUmkm(nama: String, kategori: String?, deskripsi: String?, fotoUrl: String?) {
        viewModelScope.launch {
            _umkmState.value = UmkmState.Loading

            val umkm = Umkm(
                nama = nama,
                kategori = kategori,
                deskripsi = deskripsi,
                fotoUrl = fotoUrl
            )

            val result = repository.insertUmkm(umkm)
            if (result.isSuccess) {
                _umkmState.value = UmkmState.Success("UMKM berhasil ditambahkan")
                loadAllUmkm() // Refresh list setelah insert
            } else {
                _umkmState.value = UmkmState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menambahkan UMKM"
                )
            }
        }
    }

    fun loadAllUmkm() {
        viewModelScope.launch {
            _umkmState.value = UmkmState.Loading

            val result = repository.getAllUmkm()
            if (result.isSuccess) {
                _umkmState.value = UmkmState.UmkmList(result.getOrNull() ?: emptyList())
            } else {
                _umkmState.value = UmkmState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal memuat data"
                )
            }
        }
    }

    fun resetState() {
        _umkmState.value = UmkmState.Idle
    }
}
