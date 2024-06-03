package com.example.jokeapp.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoriesResponse(
    @Json(name = "categories")
    val categories: List<String>,
    @Json(name = "categoryAliases")
    val categoryAliases: List<CategoryAliase>,
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "timestamp")
    val timestamp: Long
) {
    @JsonClass(generateAdapter = true)
    data class CategoryAliase(
        @Json(name = "alias")
        val alias: String,
        @Json(name = "resolved")
        val resolved: String
    )
}