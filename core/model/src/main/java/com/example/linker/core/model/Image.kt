package com.example.linker.core.model

data class Image(
    val id: Int,
    val imageId: String,
    val url: String,
    val breedId: String? = null,
    val width: Int,
    val height: Int
)
