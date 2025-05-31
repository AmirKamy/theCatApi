package com.linker.core.network.model

data class BreedDto(
    val id: String,
    val name: String,
    val description: String?,
    val origin: String?,
    val life_span: String?,
    val temperament: String?,
    val reference_image_id: String?,
    val isFavorite: Boolean,
    val imageUrl: String? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null
)
