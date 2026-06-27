package com.example.deuros.data.remote

object CatalogApi {
    private const val HOST = "fefu2026spring.deploy.feip.dev"
    private const val BASE_URL = "https://$HOST"
    private const val API_TOKEN = "Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"

    const val CATALOG_URL = "$BASE_URL/catalog"
    const val AUTHORIZATION_HEADER_VALUE = "Bearer $API_TOKEN"

    fun resolveImageUrl(imageUrl: String): String = when {
        imageUrl.startsWith("http://$HOST") -> imageUrl.replaceFirst("http://", "https://")
        imageUrl.startsWith("http://") || imageUrl.startsWith("https://") -> imageUrl
        imageUrl.startsWith("/") -> "$BASE_URL$imageUrl"
        imageUrl.isBlank() -> imageUrl
        else -> "$BASE_URL/$imageUrl"
    }

    fun requiresAuthorization(url: String): Boolean =
        url.startsWith(BASE_URL) || url.startsWith("http://$HOST")
}
