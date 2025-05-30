package com.example.linker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "breeds")
data class BreedEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val origin: String?,
    val lifeSpan: String?,
    val referenceImageId: String?
)