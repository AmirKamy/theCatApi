package com.example.linker.core.designsystem.component

import Breed
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun BreedRow(breed: Breed, onFavoriteToggle: () -> Unit, onBreedClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp) // ارتفاع ثابت برای جلوگیری از رندر سریع
            .padding(8.dp)
            .clickable { onBreedClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        DynamicAsyncImage(
            imageUrl = breed.imageUrl.toString(),
            contentDescription = "Product Image",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentScale = ContentScale.Crop
        )

        Column {
            Text(text = breed.name, style = MaterialTheme.typography.headlineMedium)
            // Text(text = breed.description ?: "No description available", maxLines = 1)
        }
        Icon(
            modifier = Modifier.clickable { onFavoriteToggle() },
            imageVector = if (breed.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (breed.isFavorite) Color.Red else Color.Gray
        )
    }
}