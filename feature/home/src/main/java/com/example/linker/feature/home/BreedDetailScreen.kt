package com.example.linker.feature.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    breedId: String
) {
//    val product = viewModel.selectedProduct.value
//    if (product != null)
//        ShowContent(product, viewModel, navController)

}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ShowContent(product: Product, viewModel: HomeViewModel, navController: NavController) {
//
//    val productId = product.id
//    val cartItems = viewModel.cartItems
//    val isInCart = product.id in cartItems
//
//    Scaffold(
//        topBar = {
//            LinkerTopAppBar(
//                R.string.products_detail,
//                navigationIcon = LinkerIcons.ArrowBack,
//                navigationIconContentDescription = "Back",
//                action = {
//                    IconButton(onClick = {
//                        if (isInCart) {
//                            cartItems.remove(productId)
//                        } else {
//                            cartItems.add(productId)
//                        }
//                    }) {
//                        Icon(
//                            imageVector = if (isInCart) Icons.Default.Delete else Icons.Default.Add,
//                            contentDescription = "add to cart",
//                            tint = MaterialTheme.colorScheme.onSurface,
//                        )
//                    }
//                },
//                onNavigationClick = { navController.popBackStack() },
//            )
//        }
//    ) { paddingValues ->
//        Box(modifier = Modifier.padding(paddingValues)) {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .border(
//                        width = 1.dp,
//                        color = Color.LightGray,
//                        shape = RoundedCornerShape(16.dp),
//                    ),
//                elevation = CardDefaults.cardElevation(
//                    defaultElevation = 6.dp
//                ),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                shape = RoundedCornerShape(16.dp),
//                content = {
//                    Column {
//                        DynamicAsyncImage(
//                            imageUrl = product.image,
//                            contentDescription = "Product Image",
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(200.dp)
//                                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
//                                .align(Alignment.CenterHorizontally),
//                            contentScale = ContentScale.Fit
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = product.title,
//                            style = MaterialTheme.typography.titleMedium,
//                            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = product.description.trim(),
//                            style = MaterialTheme.typography.bodyMedium,
//                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp).fillMaxWidth()
//                        )
//                    }
//
//                }
//            )
//        }
//
//    }
//}
//
