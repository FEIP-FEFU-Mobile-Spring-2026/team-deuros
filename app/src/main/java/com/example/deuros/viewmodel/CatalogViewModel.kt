package com.example.deuros.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deuros.data.models.Product
import com.example.deuros.data.repository.ProductsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: ProductsRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val categories: StateFlow<List<Pair<String, String>>> = _categories.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    val filteredProducts = combine(_products, _selectedCategoryId) { products, categoryId ->
        when {
            categoryId == null -> products
            categoryId == "new" -> products.filter { it.tags.contains("New") }
            else -> products.filter { it.categoryId == categoryId }
        }
    }

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val response = repository.loadProducts()
            if (response != null) {
                _products.value = response.items

                // Формируем список категорий с "Новинки" первой
                val categoryList = mutableListOf<Pair<String, String>>()
                categoryList.add("new" to "Новинки")
                categoryList.addAll(response.categories.map { it.id to it.name })
                _categories.value = categoryList

                // Выбираем первую категорию (Новинки)
                _selectedCategoryId.value = "new"
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    // Factory для создания ViewModel с зависимостями
    class Factory(private val repository: ProductsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
                return CatalogViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}