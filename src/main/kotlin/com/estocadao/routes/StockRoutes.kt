package com.estocadao.routes

import com.estocadao.models.CreateStockItemRequest
import com.estocadao.models.ErrorResponse
import com.estocadao.models.UpdateStockItemRequest
import com.estocadao.services.StockService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.stockRoutes(stockService: StockService) {

    route("/stock") {

        // GET /stock/summary — DEVE vir ANTES de /{id} para não ser capturado como ID
        get("/summary") {
            val summary = stockService.getStockSummary()
            call.respond(HttpStatusCode.OK, summary)
        }

        // GET /stock — Lista todos os itens de estoque
        get {
            val items = stockService.getAllStockItems()
            call.respond(HttpStatusCode.OK, items)
        }

        // GET /stock/{id} — Busca item por ID
        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "ID não informado")
                )

            val item = stockService.getStockItemById(id)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("not_found", "Item de estoque com id '$id' não encontrado")
                )

            call.respond(HttpStatusCode.OK, item)
        }

        // POST /stock — Adiciona item ao estoque
        post {
            val body = runCatching { call.receive<CreateStockItemRequest>() }.getOrElse {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "Corpo da requisição inválido: ${it.message}")
                )
            }

            if (body.productId.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "O campo 'product_id' é obrigatório")
                )
            }

            val created = stockService.createStockItem(body)
            call.respond(HttpStatusCode.Created, created)
        }

        // PUT /stock/{id} — Atualiza item do estoque
        put("/{id}") {
            val id = call.parameters["id"]
                ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "ID não informado")
                )

            val body = runCatching { call.receive<UpdateStockItemRequest>() }.getOrElse {
                return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "Corpo da requisição inválido: ${it.message}")
                )
            }

            val updated = stockService.updateStockItem(id, body)
                ?: return@put call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("not_found", "Item de estoque com id '$id' não encontrado")
                )

            call.respond(HttpStatusCode.OK, updated)
        }

        // DELETE /stock/{id} — Remove item do estoque
        delete("/{id}") {
            val id = call.parameters["id"]
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "ID não informado")
                )

            val deleted = stockService.deleteStockItem(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("not_found", "Item de estoque com id '$id' não encontrado")
                )
            }
        }
    }
}
