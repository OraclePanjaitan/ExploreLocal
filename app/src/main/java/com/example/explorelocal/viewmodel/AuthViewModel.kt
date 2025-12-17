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

    private val _isRoleLoaded = MutableStateFlow(false)
    val isRoleLoaded: StateFlow<Boolean> = _isRoleLoaded



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
            try {
                authRepository.logout()
                authRepository.clearToken(context)  // ✅ Clear token
                _userState.value = UserState.LoggedOut
                _userRole.value = null
                _isRoleLoaded.value = false  // ✅ Reset flag
                android.util.Log.d("AuthViewModel", "Logout success, token cleared")
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Logout gagal")
            }
        }
    }

    fun isUserLoggedIn(context: Context) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            _isRoleLoaded.value = false

            try {
                val loginResult = authRepository.isUserLoggedIn(context)

                if (loginResult.isSuccess && loginResult.getOrNull() == true) {

                    val userResult = authRepository.retrieveCurrentUser()

                    if (userResult.isSuccess) {
                        authRepository.refreshSession()
                        authRepository.saveToken(context)

                        _userState.value = UserState.LoggedIn(userResult.getOrNull())

                        android.util.Log.d("AuthViewModel", "User logged in, loading role...")

                        // Load role
                        loadUserRole()
                    } else {
                        authRepository.clearToken(context)
                        _userState.value = UserState.LoggedOut
                        _isRoleLoaded.value = true
                    }
                } else {
                    // User not logged in
                    _userState.value = UserState.LoggedOut
                    _isRoleLoaded.value = true
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Error checking login: ${e.message}")
                authRepository.clearToken(context)
                _userState.value = UserState.LoggedOut
                _isRoleLoaded.value = true
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
            try {
                val result = userRepository.getUserRole()
                if (result.isSuccess) {
                    _userRole.value = result.getOrNull()
                    android.util.Log.d("AuthViewModel", "Role loaded: ${_userRole.value}")
                } else {
                    android.util.Log.e("AuthViewModel", "Failed to load role: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Error loading role: ${e.message}")
            } finally {
                _isRoleLoaded.value = true
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
        expectedRole: String
    ) {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            // Login user
            val loginResult = authRepository.login(userEmail, userPassword)

            if (loginResult.isFailure) {
                _userState.value = UserState.Error(
                    loginResult.exceptionOrNull()?.message ?: "Login gagal"
                )
                return@launch
            }

            // ✅ TAMBAHKAN: Save token setelah login berhasil
            authRepository.saveToken(context)
            android.util.Log.d("AuthViewModel", "Token saved after login")

            // Check role
            val roleResult = userRepository.getUserRole()

            if (roleResult.isFailure) {
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

            val actualRole = roleResult.getOrNull()
            android.util.Log.d("AuthViewModel", "Expected: $expectedRole, Actual: $actualRole")

            if (actualRole != expectedRole) {
                authRepository.logout()
                // ✅ TAMBAHKAN: Clear token juga
                authRepository.clearToken(context)

                val errorMessage = when (actualRole) {
                    "owner" -> "Akun Anda terdaftar sebagai Pemilik UMKM. Silakan pilih menu UMKM untuk login."
                    "user" -> "Akun Anda terdaftar sebagai Penjelajah. Silakan pilih menu Penjelajah untuk login."
                    else -> "Role akun tidak valid"
                }

                _userState.value = UserState.Error(errorMessage)
            } else {
                _userRole.value = actualRole
                _userState.value = UserState.Success("Login berhasil!")
            }
        }
    }
}
