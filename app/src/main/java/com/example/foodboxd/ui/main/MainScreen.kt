package com.example.foodboxd.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodboxd.ui.detail.RestaurantDetailScreen
import com.example.foodboxd.ui.favorites.FavoritesScreen
import com.example.foodboxd.ui.home.HomeScreen
import com.example.foodboxd.ui.profile.ProfileScreen
import com.example.foodboxd.ui.search.SearchScreen
import com.example.foodboxd.ui.theme.Neutral950
import com.example.foodboxd.ui.theme.YellowPrimary
import com.example.foodboxd.ui.top.TopRestaurantsScreen

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        Triple("home", "Inicio", Icons.Default.Home),
        Triple("top", "Top", Icons.Default.Star),
        Triple("search", "Buscar", Icons.Default.Search),
        Triple("favorites", "Favoritos", Icons.Default.Favorite),
        Triple("profile", "Perfil", Icons.Default.Person)
    )

    // La barra inferior solo se muestra en las cinco secciones principales.
    val showBottomBar = currentRoute in items.map { it.first }

    fun goToDetail(restaurantId: String) {
        navController.navigate("detail/$restaurantId")
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Neutral950
                ) {
                    items.forEach { (route, title, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = title) },
                            label = { Text(title) },
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Neutral950,
                                selectedTextColor = YellowPrimary,
                                indicatorColor = YellowPrimary.copy(alpha = 0.2f),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onRestaurantClick = { id -> goToDetail(id) },
                    onSeeAllClick = {
                        navController.navigate("top") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable("top") {
                TopRestaurantsScreen(
                    onRestaurantClick = { id -> goToDetail(id) }
                )
            }
            composable("search") {
                SearchScreen(onRestaurantClick = { id -> goToDetail(id) })
            }
            composable("favorites") {
                FavoritesScreen(onRestaurantClick = { id -> goToDetail(id) })
            }
            composable("profile") {
                ProfileScreen(onLogout = onLogout)
            }
            composable(
                route = "detail/{restaurantId}",
                arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
            ) { backStackEntry ->
                val restaurantId = backStackEntry.arguments?.getString("restaurantId").orEmpty()
                RestaurantDetailScreen(
                    restaurantId = restaurantId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, color = Color.Gray)
    }
}
