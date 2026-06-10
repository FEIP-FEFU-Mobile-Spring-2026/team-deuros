package com.example.deuros.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.deuros.ui.components.ProductCard
import com.example.deuros.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle(
        initialValue = null
    )
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (categories.isNotEmpty()) {
            PrimaryScrollableTabRow(
                selectedTabIndex = categories.indexOfFirst { it.first == selectedCategoryId },
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEachIndexed { index, (id, name) ->
                    Tab(
                        selected = id == selectedCategoryId,
                        onClick = { viewModel.selectCategory(id) },
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
            items(filteredProducts) { product ->
                ProductCard(product = product)
            }
        }
    }
}