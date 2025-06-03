package com.example.linker.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.linker.core.designsystem.component.AppLoadingWheel
import com.example.linker.core.designsystem.component.BreedRow
import com.example.linker.core.designsystem.component.LinkerTopAppBar
import com.example.linker.core.model.Resource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    onBreedClick: (String) -> Unit
) {
    val lazyPagingItems = viewModel.breeds.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            LinkerTopAppBar(
                titleRes = R.string.breeds,
                navigationIcon = null,
                navigationIconContentDescription = "Back",
                action = {},
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ){
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    label = { Text("Search Breeds") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                when (val searchResult = searchResults) {
                    is Resource.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = searchResult.message,
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.retrySearch() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                    else -> {}
                }

                PullToRefreshBox(
                    isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading,
                    onRefresh = {
                        coroutineScope.launch {
                            lazyPagingItems.refresh()
                            if (searchQuery.isNotBlank()) {
                                viewModel.setSearchQuery(searchQuery)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = lazyListState,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        if (searchQuery.isNotBlank()) {
                            when (val searchResult = searchResults) {
                                is Resource.Success -> {
                                    if (searchResult.data.isEmpty()) {
                                        item {
                                            Text(
                                                text = "No results found. Try a different query.",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    } else {
                                        items(
                                            count = searchResult.data.size,
                                            key = { index -> searchResult.data[index].id }
                                        ) { index ->
                                            val breed = searchResult.data[index]
                                            BreedRow(
                                                breed = breed,
                                                onFavoriteToggle = {
                                                    viewModel.toggleFavorite(breed.id, !breed.isFavorite)
                                                },
                                                onBreedClick = {
                                                    viewModel.setSelectedBreed(breed)
                                                    onBreedClick(breed.id)
                                                }
                                            )
                                        }
                                    }
                                }
                                is Resource.Loading -> {
                                    item {
                                        AppLoadingWheel(
                                            contentDesc = "Search Query Loading",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        )
                                    }
                                }
                                else -> {}
                            }
                        } else {
                            items(
                                count = lazyPagingItems.itemCount,
                                key = { index -> lazyPagingItems[index]?.id ?: "breed-$index" }
                            ) { index ->
                                lazyPagingItems[index]?.let { breed ->
                                    BreedRow(
                                        breed = breed,
                                        onFavoriteToggle = {
                                            viewModel.toggleFavorite(breed.id, !breed.isFavorite)
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
                                            AppLoadingWheel(
                                                contentDesc = "Breed Loading",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                    refresh is LoadState.Error -> {
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "Failed to load breeds. Please try again.",
                                                    textAlign = TextAlign.Center
                                                )
                                                Button(
                                                    onClick = { lazyPagingItems.refresh() },
                                                    modifier = Modifier.padding(top = 8.dp)
                                                ) {
                                                    Text("Retry")
                                                }
                                            }
                                        }
                                    }
                                    append is LoadState.Loading -> {
                                        item {
                                            AppLoadingWheel(
                                                contentDesc = "Breed Loading",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                    append is LoadState.Error -> {
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "Failed to load more breeds. Please try again.",
                                                    textAlign = TextAlign.Center
                                                )
                                                Button(
                                                    onClick = { lazyPagingItems.retry() },
                                                    modifier = Modifier.padding(top = 8.dp)
                                                ) {
                                                    Text("Retry")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }


//        if (searchQuery.isNotBlank()) {
//            viewModel.setSearchQuery(searchQuery)
//        }

}