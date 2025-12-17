package com.example.explorelocal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.data.repository.AuthRepository
import com.example.explorelocal.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole



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

    fun setUserRole(context: Context, role: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.setUserRole(role)
                if (result.isSuccess) {
                    _userRole.value = role
                    android.util.Log.d("AuthViewModel", "Role set to: $role")
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Error setting role: ${e.message}")
            }
        }
    }

    fun loadUserRole() {
        viewModelScope.launch {
            val result = userRepository.getUserRole()
            if (result.isSuccess) {
                _userRole.value = result.getOrNull()
            }
        }
    }

    /**
     * Login dengan validasi role
     */
    fun loginWithRoleValidation(
        context: Context,
        userEmail: String,
        userPassword: String,
        expectedRole: String  // ✅ Role yang dipilih user di OnboardingRole
    ) {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            // 1. Login dulu
            val loginResult = authRepository.login(userEmail, userPassword)

            if (loginResult.isFailure) {
                _userState.value = UserState.Error(
                    loginResult.exceptionOrNull()?.message ?: "Login gagal"
                )
                return@launch
            }

            // 2. Login berhasil, cek role di database
            val roleResult = userRepository.getUserRole()

            if (roleResult.isFailure) {
                // User belum punya role (kemungkinan akun baru), set role sesuai pilihan
                android.util.Log.d("AuthViewModel", "No role found, setting to: $expectedRole")
                val setRoleResult = userRepository.setUserRole(expectedRole)

                if (setRoleResult.isSuccess) {
                    _userRole.value = expectedRole
                    _userState.value = UserState.Success("Login berhasil!")
                } else {
                    _userState.value = UserState.Error("Gagal menyimpan role")
                }
                return@launch
            }

            // 3. Role ada, validasi apakah sesuai dengan pilihan
            val actualRole = roleResult.getOrNull()
            android.util.Log.d("AuthViewModel", "Expected: $expectedRole, Actual: $actualRole")

            if (actualRole != expectedRole) {
                // ❌ Role tidak sesuai
                authRepository.logout()  // Logout otomatis

                val errorMessage = when (actualRole) {
                    "owner" -> "Akun Anda terdaftar sebagai Pemilik UMKM. Silakan pilih menu UMKM untuk login."
                    "user" -> "Akun Anda terdaftar sebagai Penjelajah. Silakan pilih menu Penjelajah untuk login."
                    else -> "Role akun tidak valid"
                }

                _userState.value = UserState.Error(errorMessage)
            } else {
                // ✅ Role sesuai
                _userRole.value = actualRole
                _userState.value = UserState.Success("Login berhasil!")
            }
        }
    }

}
