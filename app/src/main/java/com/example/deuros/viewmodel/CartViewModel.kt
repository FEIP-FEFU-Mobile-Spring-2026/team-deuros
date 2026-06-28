package com.example.deuros.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val CART_PREFS_NAME = "cart_prefs"
private const val CART_ITEMS_KEY = "cart_items"

data class CartItem(
    val productId: String,
    val sizeId: String,
    val quantity: Int
)

class CartViewModel(context: Context) : ViewModel() {

    private val prefs = context.applicationContext.getSharedPreferences(
        CART_PREFS_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    private val _items = MutableStateFlow(loadItems())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun addItem(productId: String, sizeId: String) {
        updateItems { items ->
            val existingItem = items.firstOrNull { it.productId == productId && it.sizeId == sizeId }
            if (existingItem == null) {
                items + CartItem(productId = productId, sizeId = sizeId, quantity = 1)
            } else {
                items.map { item ->
                    if (item.productId == productId && item.sizeId == sizeId) {
                        item.copy(quantity = item.quantity + 1)
                    } else {
                        item
                    }
                }
            }
        }
    }

    fun incrementItem(productId: String, sizeId: String) {
        updateItems { items ->
            items.map { item ->
                if (item.productId == productId && item.sizeId == sizeId) {
                    item.copy(quantity = item.quantity + 1)
                } else {
                    item
                }
            }
        }
    }

    fun decrementItem(productId: String, sizeId: String) {
        updateItems { items ->
            items.mapNotNull { item ->
                if (item.productId == productId && item.sizeId == sizeId) {
                    val newQuantity = item.quantity - 1
                    if (newQuantity > 0) item.copy(quantity = newQuantity) else null
                } else {
                    item
                }
            }
        }
    }

    fun removeItem(productId: String, sizeId: String) {
        updateItems { items ->
            items.filterNot { it.productId == productId && it.sizeId == sizeId }
        }
    }

    fun clearCart() {
        setItems(emptyList())
    }

    private fun updateItems(transform: (List<CartItem>) -> List<CartItem>) {
        setItems(transform(_items.value).filter { it.quantity > 0 })
    }

    private fun setItems(items: List<CartItem>) {
        _items.value = items
        prefs.edit()
            .putString(CART_ITEMS_KEY, gson.toJson(items))
            .apply()
    }

    private fun loadItems(): List<CartItem> {
        val json = prefs.getString(CART_ITEMS_KEY, null) ?: return emptyList()
        return runCatching {
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson<List<CartItem>>(json, type)
                .orEmpty()
                .filter { it.productId.isNotBlank() && it.sizeId.isNotBlank() && it.quantity > 0 }
        }.getOrDefault(emptyList())
    }

    class Factory(context: Context) : ViewModelProvider.Factory {
        private val appContext = context.applicationContext

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                return CartViewModel(appContext) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
