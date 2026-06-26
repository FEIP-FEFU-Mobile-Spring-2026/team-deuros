package com.example.deuros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deuros.data.repository.ProductsRepository
import com.example.deuros.ui.DeurosNavigation
import com.example.deuros.ui.theme.DeurosTheme
import com.example.deuros.viewmodel.CartViewModel
import com.example.deuros.viewmodel.CatalogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeurosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeurosApp()
                }
            }
        }
    }
}

@Composable
fun DeurosApp() {
    val context = LocalContext.current
    val repository = ProductsRepository(context)
    val catalogViewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModel.Factory(repository)
    )
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModel.Factory(context)
    )

    DeurosNavigation(
        catalogViewModel = catalogViewModel,
        cartViewModel = cartViewModel
    )
}
