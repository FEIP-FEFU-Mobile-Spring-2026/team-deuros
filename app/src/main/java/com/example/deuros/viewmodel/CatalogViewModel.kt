package com.example.deuros.viewmodel

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

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    val filteredProducts = combine(_products, _selectedCategoryId) { products, categoryId ->
        when {
            categoryId == null -> products
            categoryId == "new" -> products.filter { it.tags.any { tag -> tag.equals("New", ignoreCase = true) } }
            else -> products.filter { it.categoryId == categoryId }
        }
    }

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading

            runCatching { repository.loadProducts() }
                .onSuccess { response ->
                    _products.value = response.items

                    val categoryList = buildList {
                        add("new" to "Новинки")
                        addAll(response.categories.map { it.id to it.name })
                    }
                    _categories.value = categoryList
                    _selectedCategoryId.value = categoryList.firstOrNull()?.first
                    _uiState.value = CatalogUiState.Content
                }
                .onFailure { throwable ->
                    _products.value = emptyList()
                    _categories.value = emptyList()
                    _selectedCategoryId.value = null
                    _uiState.value = CatalogUiState.Error(
                        message = throwable.message ?: "Не удалось загрузить каталог"
                    )
                }
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

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

sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    data object Content : CatalogUiState
    data class Error(val message: String) : CatalogUiState
}
