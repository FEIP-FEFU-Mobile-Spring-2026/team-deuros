package com.example.deuros.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.deuros.R
import com.example.deuros.ui.cart.CartScreen
import com.example.deuros.ui.catalog.CatalogScreen
import com.example.deuros.viewmodel.CartViewModel
import com.example.deuros.viewmodel.CatalogViewModel

sealed class Screen(val route: String, val title: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Catalog : Screen("catalog", R.string.catalog, Icons.Default.Store)
    object Cart : Screen("cart", R.string.cart, Icons.Default.ShoppingCart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeurosNavigation(
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val cartItems by cartViewModel.items.collectAsStateWithLifecycle()
    val products by catalogViewModel.products.collectAsStateWithLifecycle()
    val cartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(Screen.Catalog, Screen.Cart)

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            if (screen == Screen.Cart && cartCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text(text = if (cartCount > 99) "99+" else cartCount.toString())
                                        }
                                    }
                                ) {
                                    Icon(screen.icon, contentDescription = null)
                                }
                            } else {
                                Icon(screen.icon, contentDescription = null)
                            }
                        },
                        label = { Text(stringResource(screen.title)) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Catalog.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    viewModel = catalogViewModel,
                    onAddToCart = { product, size ->
                        cartViewModel.addItem(
                            productId = product.id,
                            sizeId = size.id
                        )
                    }
                )
            }
            composable(Screen.Cart.route) {
                CartScreen(
                    products = products,
                    cartItems = cartItems,
                    onIncrement = cartViewModel::incrementItem,
                    onDecrement = cartViewModel::decrementItem,
                    onRemove = cartViewModel::removeItem,
                    onClearCart = cartViewModel::clearCart
                )
            }
        }
    }
}
