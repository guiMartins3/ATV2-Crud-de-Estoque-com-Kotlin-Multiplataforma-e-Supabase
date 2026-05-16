package com.estocadao.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── PRODUCT ────────────────────────────────────────────────────────────────

@Serializable
data class Product(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val sku: String,
    val category: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String? = null,
    val sku: String,
    val category: String? = null
)

@Serializable
data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val sku: String? = null,
    val category: String? = null,
    @SerialName("updated_at") val updatedAt: String = java.time.Instant.now().toString()
)

// ─── STOCK ITEM ──────────────────────────────────────────────────────────────

@Serializable
data class StockItem(
    val id: String? = null,
    @SerialName("product_id") val productId: String,
    val quantity: Int,
    @SerialName("unit_price") val unitPrice: Double,
    val location: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class CreateStockItemRequest(
    @SerialName("product_id") val productId: String,
    val quantity: Int,
    @SerialName("unit_price") val unitPrice: Double,
    val location: String? = null
)

@Serializable
data class UpdateStockItemRequest(
    @SerialName("product_id") val productId: String? = null,
    val quantity: Int? = null,
    @SerialName("unit_price") val unitPrice: Double? = null,
    val location: String? = null,
    @SerialName("updated_at") val updatedAt: String = java.time.Instant.now().toString()
)

// ─── STOCK SUMMARY ──────────────────────────────────────────────────────────

@Serializable
data class StockSummary(
    @SerialName("product_id") val productId: String,
    @SerialName("product_name") val productName: String,
    @SerialName("total_quantity") val totalQuantity: Long
)

// ─── ERROR RESPONSE ──────────────────────────────────────────────────────────

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)
