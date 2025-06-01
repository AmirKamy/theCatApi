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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.linker.core.designsystem.component.BreedRow
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
//    val lazyPagingItems = viewModel.breeds.collectAsLazyPagingItems()
    val lazyPagingItems = remember { viewModel.breeds }.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState(initial = emptyList())
    val searchError by viewModel.searchError.collectAsState()

    val listState = rememberLazyListState()

    Log.i("HomeScreen", "Item count: ${lazyPagingItems.itemCount}")



    Scaffold(
        topBar = {
            LinkerTopAppBar(
                titleRes = R.string.breeds,
                navigationIcon = null,
                navigationIconContentDescription = "Back",
                action = {
//                    Row(
//                        modifier = Modifier.padding(end = 12.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            modifier = Modifier.padding(top = 5.dp),
//                            text = "hi",
//                            style = MaterialTheme.typography.labelLarge
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Icon(
//                            painter = painterResource(id = R.drawable.cart),
//                            contentDescription = "Cart",
//                            modifier = Modifier.size(24.dp)
//                        )
//                    }
                },
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text("Search Breeds") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            searchError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { viewModel.clearSearchError() } // پاک کردن ارور با کلیک
                )
            }


            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (searchQuery.isNotBlank()) {

                    if (searchResults.isEmpty() && searchError == null && lazyPagingItems.loadState.refresh is LoadState.NotLoading) {
                        item {
                            Text(
                                text = "No results found",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    } else {
                        items(
                            count = searchResults.size,
                            key = { index -> searchResults[index].id }
                        ) { index ->
                            val breed = searchResults[index]
                            BreedRow(
                                breed = breed,
                                onFavoriteToggle = {
                                    viewModel.toggleFavorite(
                                        breed.id,
                                        !breed.isFavorite
                                    )
                                },
                                onBreedClick = {
                                    viewModel.setSelectedBreed(breed)
                                    onBreedClick(breed.id)
                                }
                            )
                        }
                    }
                } else {
                    items(
                        count = lazyPagingItems.itemCount,
                        key = { index -> lazyPagingItems[index]?.id ?: "breed-$index" }
                    ) { index ->
                        lazyPagingItems[index]?.let { breed ->
                            Log.i("HomeScreen", "Rendering breed: ${breed.name}")
                            BreedRow(
                                breed = breed,
                                onFavoriteToggle = {
                                    viewModel.toggleFavorite(
                                        breed.id,
                                        !breed.isFavorite
                                    )
                                },
                                onBreedClick = {
                                    viewModel.setSelectedBreed(breed)
                                    onBreedClick(breed.id)
                                }
                            )
                        }
                    }
                    lazyPagingItems.loadState.apply {
                        when {
                            refresh is LoadState.Loading -> {
                                item {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }

                            refresh is LoadState.Error -> {
                                item {
                                    Text(
                                        text = "Failed to load breeds: ${(refresh as LoadState.Error).error.message}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }

                            append is LoadState.Loading -> {
                                item {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }

                            append is LoadState.Error -> {
                                item {
                                    Text(
                                        text = "Failed to load more breeds: ${(append as LoadState.Error).error.message}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}