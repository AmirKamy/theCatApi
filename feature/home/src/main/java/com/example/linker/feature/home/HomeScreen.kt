package com.example.linker.feature.home

import Breed
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.linker.core.designsystem.component.DynamicAsyncImage
import com.example.linker.core.designsystem.component.LinkerTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    onBreedClick: (String) -> Unit
) {
    val lazyPagingItems = viewModel.breeds.collectAsLazyPagingItems()
    Log.i("HomeScreen", "Item count: ${lazyPagingItems.itemCount}")

    Scaffold(
        topBar = {
            LinkerTopAppBar(
                titleRes = R.string.products,
                navigationIcon = null,
                navigationIconContentDescription = "Back",
                action = {
                    Row(
                        modifier = Modifier.padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = "hi",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.cart),
                            contentDescription = "Cart",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else if (lazyPagingItems.loadState.refresh is LoadState.Error) {
                item {
                    Text(
                        text = "Failed to load breeds: ${(lazyPagingItems.loadState.refresh as LoadState.Error).error.message}",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else if (lazyPagingItems.itemCount == 0) {
                item {
                    Text(
                        text = "No breeds available",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(
                count = lazyPagingItems.itemCount,
                key = { index ->
                    lazyPagingItems.itemSnapshotList.items.getOrNull(index)?.id ?: index
                }
            ) { index ->
                lazyPagingItems[index]?.let { breed ->
                    Log.i("HomeScreen", "Rendering breed: ${breed.name}")
                    BreedRow(
                        breed = breed,
                        onFavoriteToggle = { viewModel.toggleFavorite(breed.id, !breed.isFavorite) },
                        onBreedClick = {
                            onBreedClick(breed.id)
                        }
                    )
                }
            }

            when (lazyPagingItems.loadState.append) {
                is LoadState.Loading -> {
                    Log.i("HomeScreen", "Loading next page")
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
                is LoadState.Error -> {
                    Log.e("HomeScreen", "Error loading next page: ${(lazyPagingItems.loadState.append as LoadState.Error).error.message}")
                    item {
                        Text(
                            text = "Error loading breeds",
                            color = Color.Red,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                else -> {
                    Log.i("HomeScreen", "No append action")
                }
            }
        }
    }
}

@Composable
fun BreedRow(breed: Breed, onFavoriteToggle: () -> Unit, onBreedClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // ارتفاع ثابت برای جلوگیری از رندر سریع
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