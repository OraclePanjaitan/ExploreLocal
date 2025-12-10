package com.example.explorelocal.data.repository

import com.example.explorelocal.data.model.Umkm
import com.example.explorelocal.data.network.SupabaseClient.client
import io.github.jan.supabase.postgrest.from

class UmkmRepository {

    suspend fun insertUmkm(umkm: Umkm): Result<Umkm> {
        return try {
            val response = client.from("umkm")
                .insert(umkm) {
                    select() // Untuk return data yang baru diinsert
                }

            val insertedData = response.decodeSingle<Umkm>()
            Result.success(insertedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUmkm(): Result<List<Umkm>> {
        return try {
            val response = client.from("umkm")
                .select()
                .decodeList<Umkm>()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
