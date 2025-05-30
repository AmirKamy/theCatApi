package com.example.linker.feature.home.navigation

sealed class HomeDestination(val route: String) {
    data object HomeScreen : HomeDestination("home_screen")
    data object DetailScreen : HomeDestination("detail_screen/{breed_id}") {
        fun withArgs(breedId: String) = "detail_screen/$breedId"
    }
}