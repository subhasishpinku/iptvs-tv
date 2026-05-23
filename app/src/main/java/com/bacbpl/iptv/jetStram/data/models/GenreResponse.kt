// data/models/GenreResponse.kt
package com.bacbpl.iptv.jetStram.data.models

import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<Genre>
)

data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("categories")
    val categories: List<Category>
)

data class Category(
    @SerializedName("id")
    val id: Int,
    @SerializedName("genre_id")
    val genreId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: Int
)