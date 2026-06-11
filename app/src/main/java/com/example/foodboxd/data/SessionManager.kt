package com.example.foodboxd.data

import android.content.Context
import com.example.foodboxd.data.remote.dto.UserDto

/**
 * Guarda la sesión del usuario autenticado (token JWT + datos básicos) en
 * `SharedPreferences`, de modo que la sesión sobreviva al cierre de la app.
 * El token se inyecta en cada petición protegida mediante el interceptor de OkHttp.
 */
class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("foodboxd_session", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        private set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    val userId: String? get() = prefs.getString(KEY_USER_ID, null)
    val userName: String? get() = prefs.getString(KEY_USER_NAME, null)
    val userEmail: String? get() = prefs.getString(KEY_USER_EMAIL, null)

    val isLoggedIn: Boolean get() = !token.isNullOrBlank() && !userId.isNullOrBlank()

    fun saveSession(token: String, user: UserDto) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_EMAIL, user.email)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
    }
}
