package com.estocadao.routes

import com.estocadao.models.CreateProductRequest
import com.estocadao.models.ErrorResponse
import com.estocadao.models.UpdateProductRequest
import com.estocadao.services.ProductService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.productRoutes(productService: ProductService) {

    route("/products") {

        // GET /products — Lista todos os produtos
        get {
            val products = productService.getAllProducts()
            call.respond(HttpStatusCode.OK, products)
        }

        // GET /products/{id} — Busca produto por ID
        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "ID não informado")
                )

            val product = productService.getProductById(id)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("not_found", "Produto com id '$id' não encontrado")
                )

            call.respond(HttpStatusCode.OK, product)
        }

        // POST /products — Cadastra novo produto
        post {
            val body = runCatching { call.receive<CreateProductRequest>() }.getOrElse {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "Corpo da requisição inválido: ${it.message}")
                )
            }

            if (body.name.isBlank() || body.sku.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "Os campos 'name' e 'sku' são obrigatórios")
                )
            }

            val created = productService.createProduct(body)
            call.respond(HttpStatusCode.Created, created)
        }

        // PUT /products/{id} — Atualiza produto
        put("/{id}") {
            val id = call.parameters["id"]
                ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "ID não informado")
                )

            val body = runCatching { call.receive<UpdateProductRequest>() }.getOrElse {
                return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "Corpo da requisição inválido: ${it.message}")
                )
            }

            val updated = productService.updateProduct(id, body)
                ?: return@put call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("not_found", "Produto com id '$id' não encontrado")
                )

            call.respond(HttpStatusCode.OK, updated)
        }

        // DELETE /products/{id} — Remove produto
        delete("/{id}") {
            val id = call.parameters["id"]
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "ID não informado")
                )

            val deleted = productService.deleteProduct(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("not_found", "Produto com id '$id' não encontrado")
                )
            }
        }
    }
}
