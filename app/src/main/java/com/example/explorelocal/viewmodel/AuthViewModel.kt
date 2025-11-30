package com.example.explorelocal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.explorelocal.data.model.AuthRequest
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel yang bertanggung jawab untuk semua state autentikasi.
 * Menerima AuthRepository melalui Dependency Injection.
 * MENGGANTIKAN SupabaseAuthViewModel yang lama.
 */
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    // Gunakan StateFlow (Jetpack Compose standard) daripada State
    private val _userStateFlow = MutableStateFlow<UserState>(UserState.Loading)
    val userStateFlow: StateFlow<UserState> = _userStateFlow

    init {
        // Pengecekan sesi langsung saat ViewModel dibuat (untuk Splash Screen)
        checkSession()
    }

    fun signUp(request: AuthRequest){
        viewModelScope.launch {
            _userStateFlow.value = UserState.Loading
            val result = authRepository.signUp(request)
            _userStateFlow.value = result
        }
    }

    fun login(request: AuthRequest){
        viewModelScope.launch {
            _userStateFlow.value = UserState.Loading
            val result = authRepository.signIn(request)
            _userStateFlow.value = result
        }
    }

    fun logout(){
        viewModelScope.launch {
            _userStateFlow.value = UserState.Loading
            val result = authRepository.signOut()
            _userStateFlow.value = result
        }
    }

    fun checkSession(){
        viewModelScope.launch {
            _userStateFlow.value = UserState.Loading
            val result = authRepository.checkSession()
            _userStateFlow.value = result
        }
    }

    // Fungsi helper untuk mereset state error setelah ditampilkan
    fun resetState() {
        // Atur state ke LoggedOut jika error terjadi dan user belum login
        if (_userStateFlow.value is UserState.Error) {
            _userStateFlow.value = UserState.LoggedOut
        }
    }
}