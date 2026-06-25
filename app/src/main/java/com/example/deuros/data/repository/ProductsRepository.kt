package com.example.deuros.data.repository

import android.content.Context
import com.example.deuros.data.models.ProductsResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ProductsRepository(context: Context) {

    private val gson = Gson()

    suspend fun loadProducts(): ProductsResponse = withContext(Dispatchers.IO) {
        val connection = (URL(CATALOG_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = NETWORK_TIMEOUT_MS
            readTimeout = NETWORK_TIMEOUT_MS
            setRequestProperty("Authorization", "Bearer $API_TOKEN")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val responseCode = connection.responseCode
            if (responseCode !in HTTP_SUCCESS_RANGE) {
                throw IOException("Catalog request failed with code $responseCode")
            }

            val json = connection.inputStream.bufferedReader().use { it.readText() }
            gson.fromJson(json, ProductsResponse::class.java)
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val CATALOG_URL = "https://fefu2026spring.deploy.feip.dev/catalog"
        const val API_TOKEN = "-Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"
        const val NETWORK_TIMEOUT_MS = 15_000
        val HTTP_SUCCESS_RANGE = 200..299
    }
}
