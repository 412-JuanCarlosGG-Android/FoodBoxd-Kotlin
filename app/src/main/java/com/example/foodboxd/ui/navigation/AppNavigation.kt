package com.example.foodboxd.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodboxd.ui.auth.LoginScreen
import com.example.foodboxd.ui.main.MainScreen

/**
 * Rutas de nivel raíz de la app.
 */
object AppRoutes {
    const val LOGIN = "login"
    const val MAIN = "main"
}

/**
 * Navegación raíz: controla el flujo entre el login y el contenedor principal
 * (que a su vez tiene su propia barra de navegación inferior).
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN,
    ) {
        composable(AppRoutes.LOGIN) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    onSignInClick = {
                        navController.navigate(AppRoutes.MAIN) {
                            // Saca el login del back stack para que el botón "atrás"
                            // no vuelva a la pantalla de inicio de sesión.
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    },
                )
            }
        }
        composable(AppRoutes.MAIN) {
            MainScreen()
        }
    }
}
