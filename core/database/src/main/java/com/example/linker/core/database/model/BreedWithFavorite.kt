package com.example.linker.core.database.model

import Breed
import androidx.room.Embedded
import androidx.room.Ignore

data class BreedWithFavorite(
    @Embedded val breed: BreedEntity,
    val isFavorite: Boolean,
    val imageUrl: String?,
    val imageWidth: Int?,
    val imageHeight: Int?
) {
    @Ignore
    val id: String = breed.id
}


fun BreedWithFavorite.asExternalModel() = Breed(
    breed.id,
    breed.name,
    breed.description,
    breed.origin,
    breed.lifeSpan,
    breed.referenceImageId,
    isFavorite,
    imageUrl,
    imageWidth,
    imageHeight
)