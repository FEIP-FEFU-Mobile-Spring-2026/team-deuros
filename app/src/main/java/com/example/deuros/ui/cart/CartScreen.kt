package com.example.deuros.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.deuros.data.models.Product
import com.example.deuros.data.models.Size
import com.example.deuros.ui.components.ProductImage
import com.example.deuros.ui.components.formatPrice
import com.example.deuros.viewmodel.CartItem

private val Brown = Color(0xFFAD7C68)
private val TextSecondary = Color(0xFF717B8E)

data class CartLine(
    val product: Product,
    val size: Size,
    val quantity: Int
)

@Composable
fun CartScreen(
    products: List<Product>,
    cartItems: List<CartItem>,
    onIncrement: (String, String) -> Unit,
    onDecrement: (String, String) -> Unit,
    onRemove: (String, String) -> Unit,
    onClearCart: () -> Unit
) {
    var showClearDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val cartLines = remember(products, cartItems) {
        cartItems.mapNotNull { item ->
            val product = products.firstOrNull { it.id == item.productId }
            val size = product?.sizes?.firstOrNull { it.id == item.sizeId }
            if (product != null && size != null) {
                CartLine(product = product, size = size, quantity = item.quantity)
            } else {
                null
            }
        }
    }
    val totalPriceInKopecks = cartLines.sumOf { it.product.priceInKopecks * it.quantity }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(text = "Очистить корзину?") },
            text = { Text(text = "Все товары будут удалены из корзины.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearCart()
                        showClearDialog = false
                    }
                ) {
                    Text(text = "Очистить", color = Brown)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(text = "Отмена")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Text(
                    text = "Заказ успешно оформлен",
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(text = "Подтверждение и чек отправили на вашу почту") },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Brown)
                ) {
                    Text(text = "Вернуться на главную")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        CartHeader(
            showClearButton = cartItems.isNotEmpty(),
            onClearClick = { showClearDialog = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            cartItems.isEmpty() -> {
                CartEmptyState(modifier = Modifier.weight(1f))
            }

            cartLines.isEmpty() -> {
                CartRestoringState(modifier = Modifier.weight(1f))
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = cartLines,
                        key = { "${it.product.id}_${it.size.id}" }
                    ) { line ->
                        CartLineItem(
                            line = line,
                            onIncrement = { onIncrement(line.product.id, line.size.id) },
                            onDecrement = { onDecrement(line.product.id, line.size.id) },
                            onRemove = { onRemove(line.product.id, line.size.id) }
                        )
                    }
                }

                CartSummary(
                    totalPriceInKopecks = totalPriceInKopecks,
                    onCheckout = {
                        onClearCart()
                        showSuccessDialog = true
                    }
                )
            }
        }
    }
}

@Composable
private fun CartHeader(
    showClearButton: Boolean,
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Корзина",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (showClearButton) {
            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Очистить корзину",
                    tint = Brown
                )
            }
        }
    }
}

@Composable
private fun CartLineItem(
    line: CartLine,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProductImage(
                product = line.product,
                modifier = Modifier.size(width = 88.dp, height = 110.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = line.product.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить позицию",
                            tint = TextSecondary
                        )
                    }
                }

                Text(
                    text = "Размер: ${line.size.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Text(
                    text = formatPrice(line.product.priceInKopecks * line.quantity),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Brown
                )

                QuantityControl(
                    quantity = line.quantity,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement
                )
            }
        }
    }
}

@Composable
private fun QuantityControl(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF7EFEC)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Уменьшить количество",
                    tint = Brown
                )
            }

            Text(
                text = quantity.toString(),
                modifier = Modifier.width(28.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Увеличить количество",
                    tint = Brown
                )
            }
        }
    }
}

@Composable
private fun CartSummary(
    totalPriceInKopecks: Int,
    onCheckout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Итого",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Text(
                text = formatPrice(totalPriceInKopecks),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onCheckout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Brown)
        ) {
            Text(
                text = "Оформить",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CartEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Brown
            )
            Text(
                text = "Корзина пока пуста",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Добавляйте товары из каталога",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CartRestoringState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Восстанавливаем товары из каталога...",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
