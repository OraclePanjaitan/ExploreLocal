package com.example.explorelocal.data.network


import com.example.explorelocal.BuildConfig
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.createSupabaseClient

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.supabaseUrl,
        supabaseKey = BuildConfig.supabaseKey
    ){
        install(GoTrue)
    }
}