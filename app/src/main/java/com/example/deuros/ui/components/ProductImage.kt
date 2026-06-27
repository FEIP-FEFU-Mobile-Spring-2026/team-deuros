package com.example.deuros.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.deuros.data.models.Product
import com.example.deuros.data.remote.CatalogApi

private const val TAG = "ProductImage"

@Composable
fun ProductImage(
    product: Product,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current
    val imageUrl = remember(product.imageUrl) {
        CatalogApi.resolveImageUrl(product.imageUrl)
    }
    val imageRequest = remember(context, imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .apply {
                if (CatalogApi.requiresAuthorization(imageUrl)) {
                    addHeader("Authorization", CatalogApi.AUTHORIZATION_HEADER_VALUE)
                }
            }
            .addHeader("Accept", "image/*, image/svg+xml")
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = product.name,
        modifier = modifier,
        contentScale = contentScale,
        onError = { state ->
            Log.e(
                TAG,
                "Failed to load image for product ${product.id}: $imageUrl",
                state.result.throwable
            )
        }
    )
}
