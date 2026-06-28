package com.example.deuros.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.deuros.data.models.Product
import com.example.deuros.data.models.Size

private val SheetBackground = Color(0xFFFFFFFF)
private val Brown = Color(0xFFAD7C68)
private val DarkBrown = Color(0xFF6D3F2C)
private val TextPrimary = Color(0xFF171717)
private val TextSecondary = Color(0xFF8A8A8A)
private val ChipBackground = Color(0xFFF7F7F7)
private val DividerColor = Color(0xFFEDE8E5)
private val InfoBackground = Color(0xFFF7EFEC)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailsBottomSheet(
    product: Product,
    onAddToCart: (Product, Size) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSize by remember(product.id) { mutableStateOf(product.sizes.firstOrNull()) }
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        ProductInfoDialog(
            product = product,
            onDismiss = { showInfoDialog = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 680.dp)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp)
        ) {
            ProductImageHeader(product = product)

            ProductTitleBlock(
                product = product,
                onInfoClick = { showInfoDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SheetBackground)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SizeSelector(
                    sizes = product.sizes,
                    selectedSize = selectedSize,
                    onSizeClick = { selectedSize = it }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DividerColor)
                )

                Button(
                    onClick = {
                        selectedSize?.let { size ->
                            onAddToCart(product, size)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = selectedSize != null,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Brown,
                        disabledContainerColor = Brown.copy(alpha = 0.45f)
                    )
                ) {
                    Text(
                        text = "В корзину · ${formatPrice(product.priceInKopecks)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductImageHeader(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 28.dp),
            contentScale = ContentScale.Fit
        )

        if (product.tags.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                product.tags.forEach { tag ->
                    ProductTagChip(tag = tag.uppercase())
                }
            }
        }
    }
}

@Composable
private fun ProductTitleBlock(
    product: Product,
    onInfoClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = product.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .clickable(onClick = onInfoClick),
                shape = CircleShape,
                color = InfoBackground,
                contentColor = Brown
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "i",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Text(
            text = product.shortDescription.ifBlank { product.longDescription },
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SizeSelector(
    sizes: List<Size>,
    selectedSize: Size?,
    onSizeClick: (Size) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sizes.forEach { size ->
            val selected = size.id == selectedSize?.id
            Surface(
                modifier = Modifier
                    .height(36.dp)
                    .clickable { onSizeClick(size) },
                shape = RoundedCornerShape(18.dp),
                color = if (selected) DarkBrown else ChipBackground,
                contentColor = if (selected) Color.White else TextPrimary
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = size.name.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductTagChip(tag: String) {
    Surface(
        color = Brown,
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ProductInfoDialog(
    product: Product,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Характеристики",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProductInfoRow(label = "Материал", value = product.material.orEmptyValue())
                ProductInfoRow(label = "Вес", value = product.weight.orEmptyValue())
                ProductInfoRow(label = "Сезон", value = product.season.orEmptyValue())
                ProductInfoRow(label = "Страна производства", value = product.countryOfOrigin.orEmptyValue())
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Закрыть", color = Brown)
            }
        }
    )
}

@Composable
private fun ProductInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.End
        )
    }
}

private fun String?.orEmptyValue(): String = this?.takeIf { it.isNotBlank() } ?: "—"
