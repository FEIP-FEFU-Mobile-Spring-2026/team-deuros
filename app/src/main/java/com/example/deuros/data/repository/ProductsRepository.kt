package com.example.deuros.data.repository

import android.content.Context
import com.example.deuros.data.models.ProductsResponse
import com.example.deuros.data.remote.CatalogApi
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ProductsRepository(context: Context) {

    private val gson = Gson()

    suspend fun loadProducts(): ProductsResponse = withContext(Dispatchers.IO) {
        val connection = (URL(CatalogApi.CATALOG_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = NETWORK_TIMEOUT_MS
            readTimeout = NETWORK_TIMEOUT_MS
            setRequestProperty("Authorization", CatalogApi.AUTHORIZATION_HEADER_VALUE)
            setRequestProperty("Accept", "application/json")
        }

        try {
            val responseCode = connection.responseCode
            if (responseCode !in HTTP_SUCCESS_RANGE) {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                throw IOException(
                    buildString {
                        append("Catalog request failed: HTTP ")
                        append(responseCode)
                        if (!errorBody.isNullOrBlank()) {
                            append(". Response: ")
                            append(errorBody)
                        }
                    }
                )
            }

            val json = connection.inputStream.bufferedReader().use { it.readText() }
            parseProductsResponse(json)
        } finally {
            connection.disconnect()
        }
    }

    private fun parseProductsResponse(json: String): ProductsResponse {
        if (json.isBlank()) {
            throw IOException("Catalog request failed: empty response")
        }

        return try {
            gson.fromJson(json, ProductsResponse::class.java)
                ?: throw IOException("Catalog request failed: empty JSON root")
        } catch (exception: JsonSyntaxException) {
            throw IOException("Catalog request failed: invalid JSON", exception)
        }
    }

    private companion object {
        const val NETWORK_TIMEOUT_MS = 15_000
        val HTTP_SUCCESS_RANGE = 200..299
    }
}
