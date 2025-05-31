package com.example.linker.feature.home

import Breed
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.linker.core.designsystem.component.BreedRow
import com.example.linker.core.designsystem.component.DynamicAsyncImage
import com.example.linker.core.designsystem.component.LinkerTopAppBar
import com.example.linker.core.model.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailScreen(
    navController: NavController,
    viewModel: BreedDetailViewModel,
    breedViewModel: HomeViewModel,
    breedId: String
) {
    LaunchedEffect(Unit) {
        viewModel.loadImages(breedId)
    }
    val lazyPagingItems = viewModel.detailImages.collectAsLazyPagingItems()
    val breedForDetail = breedViewModel.breedForDetail.value

    if (breedForDetail != null)
        ShowContent(lazyPagingItems, breedForDetail, navController)


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowContent(
    lazyPagingItems: LazyPagingItems<Image>,
    breedForDetail: Breed,
    navController: NavController,
    breedViewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            LinkerTopAppBar(
                titleRes = R.string.breed_detail,
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
        val listState = rememberLazyListState()

        Column(modifier = Modifier.padding(paddingValues)) {
            BreedRow(breedForDetail, onFavoriteToggle = {
                breedViewModel.toggleFavorite(breedForDetail.id, !breedForDetail.isFavorite)
            }, onBreedClick = { })

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                state = listState,
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
                            text = "Failed to load images: ${(lazyPagingItems.loadState.refresh as LoadState.Error).error.message}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                } else if (lazyPagingItems.itemCount == 0) {
                    item {
                        Text(
                            text = "No images available",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                items(
                    count = lazyPagingItems.itemCount,
                    key = { index ->
                        lazyPagingItems[index]?.id ?: "item-$index"
                    } // کلید باید پایدار باشه
                ) { index ->
                    lazyPagingItems[index]?.let { image ->
                        DynamicAsyncImage(
                            imageUrl = image.url,
                            contentDescription = "Detail Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

//            items(
//                lazyPagingItems.itemCount,
//                key = lazyPagingItems.itemKey { it.id }
//            ) { index ->
//                val imageUrl = lazyPagingItems[index]?.url
//                imageUrl?.let {
//                    DynamicAsyncImage(
//                        imageUrl = it,
//                        contentDescription = "Detail Image",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(200.dp)
//                            .clip(RoundedCornerShape(8.dp)),
//                        contentScale = ContentScale.Crop
//                    )
//                }
//
//            }


                if (lazyPagingItems.loadState.append is LoadState.Loading) {
                    item {
                        CircularProgressIndicator(
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


