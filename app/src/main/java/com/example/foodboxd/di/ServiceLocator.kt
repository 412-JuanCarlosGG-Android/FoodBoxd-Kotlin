package com.example.foodboxd.di

import android.content.Context
import com.example.foodboxd.data.FoodboxdRepository
import com.example.foodboxd.data.SessionManager
import com.example.foodboxd.data.remote.ApiClient

/**
 * Inyección de dependencias manual y mínima (sin Hilt/Dagger) acorde al tamaño
 * del proyecto. Se inicializa una sola vez en [com.example.foodboxd.FoodboxdApplication]
 * y los ViewModels obtienen el [FoodboxdRepository] desde aquí en su constructor
 * sin argumentos, lo que mantiene intactas las llamadas `viewModel()` de Compose.
 */
object ServiceLocator {

    @Volatile
    private var repositoryInstance: FoodboxdRepository? = null

    fun init(context: Context) {
        if (repositoryInstance == null) {
            synchronized(this) {
                if (repositoryInstance == null) {
                    val session = SessionManager(context)
                    val api = ApiClient.create(session)
                    repositoryInstance = FoodboxdRepository(api, session)
                }
            }
        }
    }

    val repository: FoodboxdRepository
        get() = repositoryInstance
            ?: error("ServiceLocator no inicializado. Llama a ServiceLocator.init() en Application.onCreate().")
}
