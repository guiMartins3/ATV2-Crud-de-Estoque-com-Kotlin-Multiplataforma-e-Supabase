package com.estocadao.services

import com.estocadao.SUPABASE_URL
import com.estocadao.models.CreateProductRequest
import com.estocadao.models.Product
import com.estocadao.models.UpdateProductRequest
import com.estocadao.supabaseClient
import com.estocadao.supabaseHeaders
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ProductService {

    private val baseUrl = "$SUPABASE_URL/rest/v1/products"

    suspend fun getAllProducts(): List<Product> {
        return supabaseClient.get(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
            header("Prefer", "return=representation")
        }.body()
    }

    suspend fun getProductById(id: String): Product? {
        val results: List<Product> = supabaseClient.get(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
            parameter("id", "eq.$id")
        }.body()
        return results.firstOrNull()
    }

    suspend fun createProduct(request: CreateProductRequest): Product {
        val results: List<Product> = supabaseClient.post(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
        return results.first()
    }

    suspend fun updateProduct(id: String, request: UpdateProductRequest): Product? {
        val results: List<Product> = supabaseClient.patch(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
            parameter("id", "eq.$id")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
        return results.firstOrNull()
    }

    suspend fun deleteProduct(id: String): Boolean {
        val response = supabaseClient.delete(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
            parameter("id", "eq.$id")
            header("Prefer", "return=minimal")
        }
        return response.status == HttpStatusCode.NoContent || response.status == HttpStatusCode.OK
    }
}
