package com.example.deuros.data.repository

import android.content.Context
import com.example.deuros.data.models.ProductsResponse
import com.google.gson.Gson
import java.io.IOException

class ProductsRepository(private val context: Context) {

    fun loadProducts(): ProductsResponse? {
        return try {
            val jsonString = context.assets.open("products.json")
                .bufferedReader().use { it.readText() }
            Gson().fromJson(jsonString, ProductsResponse::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}