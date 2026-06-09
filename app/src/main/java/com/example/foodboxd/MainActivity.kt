package com.example.foodboxd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.foodboxd.ui.navigation.AppNavigation
import com.example.foodboxd.ui.theme.FoodboxdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodboxdTheme {
                AppNavigation()
            }
        }
    }
}
