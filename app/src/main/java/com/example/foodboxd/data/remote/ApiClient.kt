package com.example.foodboxd.data.remote

import com.example.foodboxd.data.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Construye la instancia de Retrofit que apunta al backend local.
 *
 * `10.0.2.2` es la dirección con la que el emulador de Android alcanza el
 * `localhost` de la máquina anfitriona, donde corre `node app.js` en el puerto 3000.
 * Un dispositivo físico debería usar la IP LAN de la PC en su lugar.
 *
 * Un interceptor adjunta automáticamente el header `Authorization: Bearer <token>`
 * a cada petición cuando hay una sesión activa.
 */
object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    fun create(session: SessionManager): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val token = session.token
            val request = if (!token.isNullOrBlank()) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                original
            }
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
