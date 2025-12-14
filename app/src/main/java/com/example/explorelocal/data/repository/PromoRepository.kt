package com.example.explorelocal.data.repository

import android.content.Context
import android.net.Uri
import com.example.explorelocal.data.model.Promo
import com.example.explorelocal.data.network.SupabaseClient.client
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.util.UUID

class PromoRepository {

    suspend fun uploadBanner(uri: Uri, context: Context): Result<String> {
        return try {
            val bucket = client.storage.from("promo_images")
            val fileName = "promo/banner_${UUID.randomUUID()}.jpg"

            val bytes = context.contentResolver
                .openInputStream(uri)
                ?.readBytes()
                ?: return Result.failure(Exception("Gagal membaca file"))

            bucket.upload(path = fileName, data = bytes, upsert = false)
            Result.success(bucket.publicUrl(fileName))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertPromo(promo: Promo): Result<Promo> {
        return try {
            val response = client.from("promo")
                .insert(promo) {
                    select()
                }
            Result.success(response.decodeSingle())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllPromo(): Result<List<Promo>> {
        return try {
            val list = client.from("promo")
                .select()
                .decodeList<Promo>()

            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPromoByUmkm(umkmId: String): Result<List<Promo>> {
        return try {
            val result = client.from("promo")
                .select {
                    filter {
                        eq("umkm_id", umkmId)
                    }
                }
                .decodeList<Promo>()

            Result.success(result)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePromo(id: String, promo: Promo): Result<Promo> {
        return try {
            val response = client.from("promo")
                .update(promo) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
            Result.success(response.decodeSingle<Promo>())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePromo(id: String): Result<Boolean> {
        return try {
            client.from("promo")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}