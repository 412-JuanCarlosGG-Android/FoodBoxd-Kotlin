package com.example.foodboxd.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodboxd.di.ServiceLocator
import com.example.foodboxd.ui.auth.LoginScreen
import com.example.foodboxd.ui.auth.RegisterScreen
import com.example.foodboxd.ui.main.MainScreen

/**
 * Rutas de nivel raíz de la app.
 */
object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

/**
 * Navegación raíz: controla el flujo entre autenticación (login/registro) y el
 * contenedor principal (que a su vez tiene su propia barra de navegación inferior).
 *
 * Si ya existe una sesión guardada se entra directamente a la app.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val startDestination = if (ServiceLocator.repository.session.isLoggedIn) {
        AppRoutes.MAIN
    } else {
        AppRoutes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(AppRoutes.LOGIN) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    onLoginSuccess = {
                        navController.navigate(AppRoutes.MAIN) {
                            // Saca el login del back stack para que el botón "atrás"
                            // no vuelva a la pantalla de inicio de sesión.
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onSignUpClick = { navController.navigate(AppRoutes.REGISTER) },
                )
            }
        }
        composable(AppRoutes.REGISTER) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                RegisterScreen(
                    modifier = Modifier.padding(innerPadding),
                    onRegisterSuccess = {
                        navController.navigate(AppRoutes.MAIN) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onBackToLogin = { navController.popBackStack() },
                )
            }
        }
        composable(AppRoutes.MAIN) {
            MainScreen(
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
