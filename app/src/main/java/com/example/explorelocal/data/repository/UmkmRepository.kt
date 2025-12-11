package com.example.explorelocal.data.repository

import android.content.Context
import android.net.Uri
import com.example.explorelocal.data.model.Umkm
import com.example.explorelocal.data.network.SupabaseClient.client
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.util.UUID

class UmkmRepository {

    suspend fun uploadImage(uri: Uri, context: Context): Result<String> {
        return try {
            val bucket = client.storage.from("umkm_images")

            val fileName = "images/umkm_${UUID.randomUUID()}.jpg"

            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes == null) {
                return Result.failure(Exception("Gagal membaca file"))
            }

            val res = bucket.upload(
                path = fileName,
                data = bytes,
                upsert = false
            )

            val publicUrl = bucket.publicUrl(fileName)

            Result.success(publicUrl)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertUmkm(umkm: Umkm): Result<Umkm> {
        return try {
            val response = client.from("umkm")
                .insert(umkm) {
                    select()
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
