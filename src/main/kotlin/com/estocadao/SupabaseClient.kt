package com.estocadao

import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// Carrega variáveis do .env (se existir) ou do ambiente do sistema
private val dotenv = dotenv {
    ignoreIfMissing = true
}

val SUPABASE_URL: String = dotenv["SUPABASE_URL"]
val SUPABASE_KEY: String = dotenv["SUPABASE_KEY"]

// Cliente HTTP configurado com headers do Supabase
val supabaseClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}

// Headers padrão para todas as requisições ao Supabase
fun supabaseHeaders(): Map<String, String> = mapOf(
    "apikey" to SUPABASE_KEY,
    "Authorization" to "Bearer $SUPABASE_KEY",
    "Content-Type" to "application/json",
    "Prefer" to "return=representation"
)
