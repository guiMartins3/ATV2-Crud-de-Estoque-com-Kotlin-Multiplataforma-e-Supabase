package com.estocadao.services

import com.estocadao.SUPABASE_URL
import com.estocadao.models.CreateStockItemRequest
import com.estocadao.models.StockItem
import com.estocadao.models.StockSummary
import com.estocadao.models.UpdateStockItemRequest
import com.estocadao.supabaseClient
import com.estocadao.supabaseHeaders
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class StockService {

    private val baseUrl = "$SUPABASE_URL/rest/v1/stock_items"

    suspend fun getAllStockItems(): List<StockItem> {
        return supabaseClient.get(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
        }.body()
    }

    suspend fun getStockItemById(id: String): StockItem? {
        val results: List<StockItem> = supabaseClient.get(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
            parameter("id", "eq.$id")
        }.body()
        return results.firstOrNull()
    }

    suspend fun createStockItem(request: CreateStockItemRequest): StockItem {
        val results: List<StockItem> = supabaseClient.post(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
        return results.first()
    }

    suspend fun updateStockItem(id: String, request: UpdateStockItemRequest): StockItem? {
        val results: List<StockItem> = supabaseClient.patch(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
            parameter("id", "eq.$id")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
        return results.firstOrNull()
    }

    suspend fun deleteStockItem(id: String): Boolean {
        val response = supabaseClient.delete(baseUrl) {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
            parameter("id", "eq.$id")
            header("Prefer", "return=minimal")
        }
        return response.status == HttpStatusCode.NoContent || response.status == HttpStatusCode.OK
    }

    // Chama a view "stock_summary" criada no Supabase (GROUP BY + SUM via SQL)
    suspend fun getStockSummary(): List<StockSummary> {
        return supabaseClient.get("$SUPABASE_URL/rest/v1/stock_summary") {
            supabaseHeaders().forEach { (k, v) -> header(k, v) }
        }.body()
    }
}
