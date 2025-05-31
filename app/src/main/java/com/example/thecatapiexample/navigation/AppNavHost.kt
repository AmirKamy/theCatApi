package com.example.thecatapiexample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.linker.feature.home.HomeScreen
import com.example.linker.feature.home.HomeViewModel
import com.example.linker.feature.home.BreedDetailScreen
import com.example.linker.feature.home.BreedDetailViewModel
import com.example.linker.feature.home.navigation.HomeDestination

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.HomeScreen.route
    ) {
        composable(HomeDestination.HomeScreen.route) {
            HomeScreen(navController = navController) { breedId ->
                navController.navigate(HomeDestination.DetailScreen.withArgs(breedId))
            }
        }

        composable(
            route = HomeDestination.DetailScreen.route,
            arguments = listOf(navArgument("breed_id") {type = NavType.StringType})
        ) { backStackEntry ->
            val breedId = backStackEntry.arguments?.getString("breed_id") ?: return@composable
            val viewModel: BreedDetailViewModel = hiltViewModel()

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeDestination.HomeScreen.route)
            }
            val breedViewModel: HomeViewModel = hiltViewModel(parentEntry)

            BreedDetailScreen(
                viewModel = viewModel,
                navController = navController,
                breedViewModel = breedViewModel,
                breedId = breedId
            )
        }
    }
}
