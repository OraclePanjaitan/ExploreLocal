package com.example.explorelocal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.data.repository.AuthRepository  // ✅ Import repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()  // ✅ Gunakan repository

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    fun signUp(
        context: Context,
        userEmail: String,
        userPassword: String,
    ) {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            val result = authRepository.signUp(userEmail, userPassword)  // ✅ Pakai repository

            if (result.isSuccess) {
                authRepository.saveToken(context)
                _userState.value = UserState.Success("Registrasi berhasil! Silakan cek email untuk verifikasi.")
            } else {
                _userState.value = UserState.Error(
                    result.exceptionOrNull()?.message ?: "Registrasi gagal"
                )
            }
        }
    }

    fun login(
        context: Context,
        userEmail: String,
        userPassword: String,
    ) {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            val result = authRepository.login(userEmail, userPassword)

            if (result.isSuccess) {
                authRepository.saveToken(context)
                _userState.value = UserState.Success("Login berhasil!")
            } else {
                _userState.value = UserState.Error(
                    result.exceptionOrNull()?.message ?: "Login gagal. Periksa email dan password Anda."
                )
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            val result = authRepository.logout()  // ✅ Pakai repository

            if (result.isSuccess) {
                authRepository.clearToken(context)
                _userState.value = UserState.LoggedOut
            } else {
                _userState.value = UserState.Error(
                    result.exceptionOrNull()?.message ?: "Logout gagal"
                )
            }
        }
    }

    fun isUserLoggedIn(context: Context) {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            try {
                val token = authRepository.getToken(context)

                if (token.isNullOrEmpty()) {
                    _userState.value = UserState.LoggedOut
                } else {
                    // Retrieve user dan refresh session
                    val userResult = authRepository.retrieveCurrentUser()

                    if (userResult.isSuccess) {
                        authRepository.refreshSession()
                        authRepository.saveToken(context)

                        _userState.value = UserState.LoggedIn(userResult.getOrNull())
                    } else {
                        // Token expired atau invalid
                        authRepository.clearToken(context)
                        _userState.value = UserState.LoggedOut
                    }
                }
            } catch (e: Exception) {
                authRepository.clearToken(context)
                _userState.value = UserState.LoggedOut
            }
        }
    }
}
