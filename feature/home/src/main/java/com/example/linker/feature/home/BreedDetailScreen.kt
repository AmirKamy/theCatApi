package com.example.linker.feature.home

import Breed
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.linker.core.designsystem.component.BreedRow
import com.example.linker.core.designsystem.component.DynamicAsyncImage
import com.example.linker.core.designsystem.component.LinkerTopAppBar
import com.example.linker.core.designsystem.component.ShowBreedDetailCardView
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

    val isFavorite = rememberSaveable { mutableStateOf(breedForDetail.isFavorite) }

    Scaffold(
        topBar = {
            LinkerTopAppBar(
                titleRes = R.string.breed_detail,
                navigationIcon = null,
                navigationIconContentDescription = "Back",
                action = {
                    Icon(
                        modifier = Modifier.clickable {
                            isFavorite.value = !isFavorite.value
                            breedViewModel.toggleFavorite(breedForDetail.id, isFavorite.value)
                        }
                            .padding(end = 8.dp),
                        imageVector = if (isFavorite.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite.value) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                },
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = rememberLazyGridState(),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan(2) }) { // عرض کامل برای لودینگ
                ShowBreedDetailCardView(breedForDetail)
            }

            if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
                item(span = { GridItemSpan(2) }) { // عرض کامل برای لودینگ
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else if (lazyPagingItems.loadState.refresh is LoadState.Error) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "Failed to load images: ${(lazyPagingItems.loadState.refresh as LoadState.Error).error.message}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else if (lazyPagingItems.itemCount == 0) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "No images available",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // آیتم‌های گرید
            items(
                count = lazyPagingItems.itemCount,
                key = { index -> lazyPagingItems[index]?.id ?: "item-$index" }
            ) { index ->
                lazyPagingItems[index]?.let { image ->
                    DynamicAsyncImage(
                        imageUrl = image.url,
                        contentDescription = "Detail Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // نسبت تصویر مربعی
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // لودینگ برای بارگذاری صفحه بعدی
            if (lazyPagingItems.loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(2) }) {
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

@Composable
fun ShowGridViewOfImages(lazyPagingItems: LazyPagingItems<Image>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 1000.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // حالت لودینگ اولیه
        if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
            item(span = { GridItemSpan(2) }) { // عرض کامل برای لودینگ
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        } else if (lazyPagingItems.loadState.refresh is LoadState.Error) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Failed to load images: ${(lazyPagingItems.loadState.refresh as LoadState.Error).error.message}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else if (lazyPagingItems.itemCount == 0) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "No images available",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // آیتم‌های گرید
        items(
            count = lazyPagingItems.itemCount,
            key = { index -> lazyPagingItems[index]?.id ?: "item-$index" }
        ) { index ->
            lazyPagingItems[index]?.let { image ->
                DynamicAsyncImage(
                    imageUrl = image.url,
                    contentDescription = "Detail Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // نسبت تصویر مربعی
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // لودینگ برای بارگذاری صفحه بعدی
        if (lazyPagingItems.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(2) }) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}




