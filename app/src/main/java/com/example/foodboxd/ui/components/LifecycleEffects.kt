package com.example.foodboxd.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Ejecuta [onResume] cada vez que la pantalla vuelve a primer plano
 * (evento `ON_RESUME` de su ciclo de vida).
 *
 * Sirve para refrescar datos al regresar a una pestaña: por ejemplo, que la
 * pantalla de Favoritos vuelva a consultar el backend cuando el usuario regresa
 * tras marcar/quitar un favorito en otra pantalla.
 *
 * Como el observador se registra cuando la pantalla ya está `RESUMED`, la primera
 * carga la hace el `init` del ViewModel; este efecto cubre los regresos posteriores.
 */
@Composable
fun OnResume(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResume by rememberUpdatedState(onResume)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentOnResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
