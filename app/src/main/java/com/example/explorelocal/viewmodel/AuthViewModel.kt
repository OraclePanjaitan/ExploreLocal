package com.example.explorelocal.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.data.network.SupabaseClient
import com.example.explorelocal.data.network.SupabaseClient.client
import com.example.explorelocal.utils.SharedPreferenceHelper
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {


    private  val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    fun signUp(
        context: Context,
        userEmail: String,
        userPassword: String,
    ){
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                    client.gotrue.signUpWith(Email){
                    email = userEmail
                    password = userPassword
                }
                saveToken(context)
                _userState.value = UserState.Success("Registered User Successfully!")
            }catch (e: Exception){
                _userState.value = UserState.Error("Error:${e.message}")
            }
        }
    }
    private fun saveToken(context: Context){
        viewModelScope.launch {
            val accessToken = client.gotrue.currentAccessTokenOrNull()
            val sharedPref = SharedPreferenceHelper(context)
            sharedPref.saveStringData("accessToken",accessToken)
        }
    }

    private fun getToken(context: Context): String?{
        val sharedPref = SharedPreferenceHelper(context)
        return sharedPref.getStringData("accessToken")
    }

    fun login(
        context: Context,
        userEmail: String,
        userPassword: String,
    ){
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                client.gotrue.loginWith(Email){
                    email = userEmail
                    password = userPassword
                }
                saveToken(context)
                _userState.value = UserState.Success("Logged in successfully!")
            }catch (e: Exception){
                _userState.value = UserState.Error("Error:${e.message}")
            }
        }
    }

    fun logout(context: Context){
        val sharedPref = SharedPreferenceHelper(context)
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                client.gotrue.logout()
                sharedPref.clearPreferences()
                _userState.value = UserState.Success("Logged out successfully!")
            }catch (e: Exception){
                _userState.value = UserState.Error("Error:${e.message}")
            }
        }
    }

    fun isUserLoggedIn(
        context: Context
    ){
        viewModelScope.launch {
            try {
                val token = getToken(context)
                if(token.isNullOrEmpty()){
                    _userState.value = UserState.Error("User is not logged in!")
                }else{
                    client.gotrue.retrieveUser(token)
                    client.gotrue.refreshCurrentSession()
                    saveToken(context)
                    _userState.value = UserState.Success("User is already logged in!")
                }
            }catch (e: Exception){
                _userState.value = UserState.Error("Error:${e.message}")
            }
        }
    }
}
