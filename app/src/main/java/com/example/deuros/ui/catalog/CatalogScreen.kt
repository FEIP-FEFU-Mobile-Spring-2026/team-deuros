package com.example.deuros.ui.catalog


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.deuros.data.models.Product
import com.example.deuros.ui.components.ProductCard
import com.example.deuros.ui.components.ProductDetailsBottomSheet
import com.example.deuros.viewmodel.CatalogUiState
import com.example.deuros.viewmodel.CatalogViewModel

private val Brown = Color(0xFFAD7C68)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel
) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle(
        initialValue = null
    )
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            CatalogUiState.Loading -> CatalogLoadingState()
            is CatalogUiState.Error -> CatalogErrorState(
                onRetryClick = viewModel::loadData
            )
            CatalogUiState.Content -> CatalogContent(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                products = filteredProducts,
                onCategoryClick = viewModel::selectCategory,
                onProductClick = { selectedProduct = it }
            )
        }

        selectedProduct?.let { product ->
            ProductDetailsBottomSheet(
                product = product,
                onDismiss = { selectedProduct = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogContent(
    categories: List<Pair<String, String>>,
    selectedCategoryId: String?,
    products: List<Product>,
    onCategoryClick: (String) -> Unit,
    onProductClick: (Product) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (categories.isNotEmpty()) {
            val selectedTabIndex = categories.indexOfFirst { it.first == selectedCategoryId }
                .coerceAtLeast(0)

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { (id, name) ->
                    Tab(
                        selected = id == selectedCategoryId,
                        onClick = { onCategoryClick(id) },
                        text = { Text(name) }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
private fun CatalogLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Brown)
    }
}

@Composable
private fun CatalogErrorState(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ошибка",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4F5668)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "👞",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Произошла ошибка",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111111),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Подождите немного и повторите попытку. Или не повторяйте...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF717B8E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onRetryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Brown)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Разрываем контракт",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Нажимай в перчатках",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFD6D6D6)
        )
    }
}
