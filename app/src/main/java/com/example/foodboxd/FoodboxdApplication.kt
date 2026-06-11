package com.example.foodboxd

import android.app.Application
import com.example.foodboxd.di.ServiceLocator

/**
 * Punto de entrada de la app. Inicializa el contenedor de dependencias
 * (Retrofit, sesión y repositorio) antes de crear cualquier pantalla.
 */
class FoodboxdApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
