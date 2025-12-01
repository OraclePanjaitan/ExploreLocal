package com.example.explorelocal.data.network


import com.example.explorelocal.BuildConfig
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.supabaseUrl,
        supabaseKey = BuildConfig.supabaseKey
    ){
        install(Auth)
    }
}