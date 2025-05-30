package com.example.linker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey val id: String,
    val breedId: String,
    val url: String,
    val width: Int,
    val height: Int
)