package com.example.linker.core.designsystem.component

import Breed
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onBreedClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            DynamicAsyncImage(
                imageUrl = breed.imageUrl.toString(),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
                    .padding(start = 5.dp)
            ) {
                Text(text = breed.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    modifier = Modifier.padding(start = 1.dp, top = 4.dp),
                    text = "Origin: " + (breed.origin ?: "not available"),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    modifier = Modifier.padding(start = 1.dp, top = 4.dp),
                    text = "Life span: " + (breed.life_span ?: "No life span available"),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                modifier = Modifier.clickable { onFavoriteToggle() }
                    .padding(end = 8.dp),
                imageVector = if (breed.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (breed.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}