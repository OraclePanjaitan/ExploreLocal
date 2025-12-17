// data/repository/UserRepository.kt
package com.example.explorelocal.data.repository

import com.example.explorelocal.data.model.UserRole
import com.example.explorelocal.data.network.SupabaseClient.client
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from

class UserRepository {

    suspend fun setUserRole(role: String): Result<Unit> {
        return try {
            val userId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("User not logged in"))

            // Insert or update role
            client.from("user_roles")
                .upsert(
                    mapOf(
                        "user_id" to userId,
                        "role" to role
                    )
                )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(): Result<String> {
        return try {
            val userId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("User not logged in"))

            val result = client.from("user_roles")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<UserRole>()

            Result.success(result.role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
