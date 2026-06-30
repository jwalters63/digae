package com.example.digae.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    private const val SUPABASE_URL = "https://weeknjujydpqggwlfsci.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_zTxjm4vU__vdnS7pX-yqPg_T7GXdk0d"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }
}
