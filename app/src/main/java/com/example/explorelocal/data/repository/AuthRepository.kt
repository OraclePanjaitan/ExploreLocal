package com.example.explorelocal.data.repository

import android.content.Context
import com.example.explorelocal.data.model.UserState
import com.example.explorelocal.data.network.SupabaseClient.client
import com.example.explorelocal.utils.SharedPreferenceHelper
import io.github.jan.supabase.gotrue.auth  // ✅ Import auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo

/**
 * Repository untuk handle Auth operations dengan Supabase
 * Repository TIDAK extend ViewModel, hanya handle data operations
 */
class AuthRepository {

    suspend fun signUp(userEmail: String, userPassword: String): Result<Unit> {
        return try {
            client.auth.signUpWith(Email) {  // ✅ auth bukan gotrue
                email = userEmail
                password = userPassword
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Login user
     */
    suspend fun login(userEmail: String, userPassword: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {  // ✅ signInWith bukan loginWith
                email = userEmail
                password = userPassword
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logout user
     */
    suspend fun logout(): Result<Unit> {
        return try {
            client.auth.signOut()  // ✅ signOut bukan logout
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get current access token
     */
    fun getCurrentToken(): String? {
        return try {
            client.auth.currentAccessTokenOrNull()  // ✅ auth bukan gotrue
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Retrieve user for current session
     */
    suspend fun retrieveCurrentUser(): Result<UserInfo> {
        return try {
            val user = client.auth.retrieveUserForCurrentSession()  // ✅ Updated method
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Refresh current session
     */
    suspend fun refreshSession(): Result<Unit> {
        return try {
            client.auth.refreshCurrentSession()  // ✅ auth bukan gotrue
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Save token to SharedPreferences
     */
    fun saveToken(context: Context) {
        val accessToken = getCurrentToken()
        if (accessToken != null) {
            val sharedPref = SharedPreferenceHelper(context)
            sharedPref.saveStringData("accessToken", accessToken)
        }
    }

    /**
     * Get token from SharedPreferences
     */
    fun getToken(context: Context): String? {
        return try {
            val sharedPref = SharedPreferenceHelper(context)
            val savedToken = sharedPref.getStringData("accessToken")
            android.util.Log.d("AuthRepository", "Retrieved token from SharedPreferences: ${savedToken?.take(10)}...")
            savedToken
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error getting token: ${e.message}")
            null
        }
    }

    /**
     * Clear token from SharedPreferences
     */
    fun clearToken(context: Context) {
        val sharedPref = SharedPreferenceHelper(context)
        sharedPref.clearPreferences()
    }

    suspend fun isUserLoggedIn(context: Context): Result<Boolean> {
        return try {
            // Cek token dari SharedPreferences
            val savedToken = getToken(context)

            if (savedToken.isNullOrEmpty()) {
                android.util.Log.d("AuthRepository", "No saved token found")
                Result.success(false)
            } else {
                // Verifikasi token masih valid dengan retrieve user
                val userResult = retrieveCurrentUser()

                if (userResult.isSuccess) {
                    android.util.Log.d("AuthRepository", "User session valid")
                    Result.success(true)
                } else {
                    android.util.Log.d("AuthRepository", "Token expired, clearing...")
                    clearToken(context)
                    Result.success(false)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error checking login: ${e.message}")
            Result.success(false)
        }
    }
}
