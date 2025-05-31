package com.example.linker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.linker.core.model.Image

@Entity(tableName = "detail_images")
data class DetailImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageId: String,
    val breedId: String,
    val url: String,
    val width: Int,
    val height: Int
)

fun DetailImageEntity.toModel(): Image {
    return Image(
        id = id,
        imageId = imageId,
        breedId = breedId,
        url = url,
        width = width,
        height = height
    )
}