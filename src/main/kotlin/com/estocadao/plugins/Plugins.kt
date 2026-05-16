package com.estocadao.plugins

import com.estocadao.models.ErrorResponse
import com.estocadao.routes.productRoutes
import com.estocadao.routes.stockRoutes
import com.estocadao.services.ProductService
import com.estocadao.services.StockService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Erro não tratado", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("internal_error", cause.message ?: "Erro interno do servidor")
            )
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                ErrorResponse("not_found", "Rota não encontrada")
            )
        }
        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respond(
                status,
                ErrorResponse("method_not_allowed", "Método HTTP não permitido para esta rota")
            )
        }
    }
}

fun Application.configureRouting() {
    val productService = ProductService()
    val stockService = StockService()

    routing {
        // Health check
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "ok", "service" to "Estocadão API"))
        }

        productRoutes(productService)
        stockRoutes(stockService)
    }
}
