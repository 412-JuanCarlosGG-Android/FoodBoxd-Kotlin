# FoodBoxd — Integración Frontend ↔ Backend

FoodBoxd es una app de **reseñas y ranking de restaurantes** (un "Letterboxd para la comida").
El proyecto vive en dos repositorios independientes que ahora funcionan **conectados**:

| Carpeta | Repositorio | Stack |
|---------|-------------|-------|
| `FoodBoxd-Backend/backend-api` | API REST | Node.js + Express + Mongoose (MongoDB Atlas) |
| `FoodBoxd-Frontend` | App Android | Kotlin + Jetpack Compose (Material 3) |

La app Android consume la **API local** del backend. Antes el frontend usaba datos
falsos (mock) cableados en cada pantalla; ahora **todas** las pantallas obtienen sus
datos de la API real, conservando el diseño original del equipo de frontend.

---

## 1. Arquitectura de la conexión

```
┌──────────────────────────── App Android (emulador) ────────────────────────────┐
│                                                                                  │
│  Compose UI  →  ViewModel  →  FoodboxdRepository  →  ApiService (Retrofit)       │
│   (pantallas)   (StateFlow)     (mapea DTO→dominio)    (interfaz HTTP)            │
│                                          │                                       │
│                                   OkHttp + Interceptor JWT                       │
│                                          │                                       │
└──────────────────────────────────────────┼──────────────────────────────────────┘
                                            │  http://10.0.2.2:3000/api/...
                                            ▼
┌──────────────────────────────── Backend (Node/Express) ─────────────────────────┐
│   routes  →  controllers  →  models (Mongoose)  →  MongoDB Atlas (nube)          │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### ¿Por qué `10.0.2.2`?
Desde el **emulador de Android**, `10.0.2.2` es un alias hacia el `localhost` de la
máquina anfitriona (donde corre el backend en el puerto `3000`). `localhost` dentro
del emulador apuntaría al propio emulador, no a la PC.
*(En un dispositivo físico habría que usar la IP LAN de la PC, p. ej. `http://192.168.x.x:3000/api/`.)*

---

## 2. Cómo levantar todo localmente

### Paso 1 — Backend
```bash
cd FoodBoxd-Backend/backend-api
npm install
npm run dev        # nodemon (hot reload)  ·  o  npm start
```
Requiere un archivo `.env` (basado en `.env.example`):
```env
PORT=3000
MONGODB_URI=mongodb+srv://<usuario>:<password>@<cluster>.mongodb.net/foodboxd
JWT_SECRET=tu_clave_secreta_jwt
```
El servidor queda escuchando en `http://localhost:3000`. Si MongoDB Atlas tiene
datos, los sirve; si no, los controladores caen a un *mock* para no romper.

Prueba rápida:
```bash
curl http://localhost:3000/api/restaurants/featured
```

### Paso 2 — Frontend (emulador)
```powershell
# 1. Arranca un emulador Android (o conecta un dispositivo)
#    Android Studio → Device Manager → Run

# 2. Compila e instala la app
cd FoodBoxd-Frontend
.\gradlew.bat installDebug

# 3. Lánzala
adb shell am start -n com.example.foodboxd/.MainActivity
```
La app abre en la pantalla de **login**. Crea una cuenta con "Regístrate" o inicia
sesión; a partir de ahí todo el contenido viene del backend.

---

## 3. Capa de red del frontend (qué se agregó)

Todo el código nuevo vive bajo `app/src/main/java/com/example/foodboxd/`:

| Archivo | Rol |
|---------|-----|
| `data/remote/dto/Dtos.kt` | DTOs que reflejan el JSON del backend (`@SerializedName("_id")`, etc.) |
| `data/remote/ApiService.kt` | Interfaz Retrofit: un método `suspend` por endpoint |
| `data/remote/ApiClient.kt` | Construye Retrofit. **Base URL `http://10.0.2.2:3000/api/`** + interceptor que añade `Authorization: Bearer <token>` |
| `data/SessionManager.kt` | Guarda token JWT + usuario en `SharedPreferences` (la sesión sobrevive al cierre) |
| `data/Mappers.kt` | Convierte DTO → modelo de dominio que consume la UI |
| `data/FoodboxdRepository.kt` | Única fuente de verdad: orquesta llamadas, mapea, centraliza login/registro |
| `di/ServiceLocator.kt` | Inyección de dependencias manual (sin Hilt), inicializada en `FoodboxdApplication` |
| `ui/components/RestaurantImage.kt` | Carga fotos remotas con **Coil** (con placeholder) |

Dependencias agregadas (en `gradle/libs.versions.toml` + `app/build.gradle.kts`):
Retrofit, Converter Gson, OkHttp Logging Interceptor, Coroutines, Lifecycle ViewModel
Compose y Coil.

### Permisos y tráfico en claro
- `AndroidManifest.xml`: permiso `INTERNET` y `android:name=".FoodboxdApplication"`.
- `res/xml/network_security_config.xml`: permite HTTP en claro **solo** hacia
  `10.0.2.2` / `localhost` (necesario porque el backend local no usa HTTPS).

---

## 4. Flujo de datos (ejemplo: pantalla de inicio)

1. `HomeScreen` (Compose) observa `HomeViewModel.uiState` (`StateFlow<UiState<…>>`).
2. `HomeViewModel` llama a `repository.getHomeContent()`.
3. `FoodboxdRepository` invoca `api.getFeatured()` (y las reseñas de cada destacado),
   recibe `List<RestaurantDto>` y las convierte con `toDomain()`.
4. El `ApiService` (Retrofit) hace `GET http://10.0.2.2:3000/api/restaurants/featured`.
5. OkHttp adjunta el JWT si hay sesión; la respuesta JSON se deserializa con Gson.
6. El resultado fluye de vuelta como `UiState.Success` y Compose se recompone.

Para mutaciones (publicar reseña, marcar favorito) el patrón es el mismo, pero el
endpoint es protegido y el interceptor envía el token automáticamente.

---

## 5. Mapeo endpoint ↔ pantalla

| Pantalla | Endpoint(s) del backend |
|----------|--------------------------|
| **Login / Registro** | `POST /api/users/login`, `POST /api/users/register` (devuelven JWT) |
| **Inicio** | `GET /api/restaurants/featured` + `GET /api/reviews/restaurant/:id` (últimas reseñas) |
| **Top** | `GET /api/restaurants/ranking` |
| **Buscar** | `GET /api/restaurants/search?q=` · `GET /api/restaurants/categories` |
| **Detalle** | `GET /api/restaurants/:id` · `GET /api/reviews/restaurant/:id` · `POST /api/reviews/restaurant/:id` · `PUT /api/users/profile/:id/favorite` |
| **Favoritos** | `GET /api/users/profile/:id/favorites` · `PUT /api/users/profile/:id/favorite` |
| **Perfil** | `GET /api/users/profile/:id` · `GET /api/reviews/user/:id` · `PUT /api/users/profile/:id` (editar nombre/bio) |

> **Refresco automático:** las pantallas de Inicio, Top, Favoritos y Perfil se
> recargan solas (en segundo plano) cada vez que vuelves a ellas — así un favorito
> o una reseña que marcas en otra pantalla se reflejan de inmediato. Implementado
> con el helper `ui/components/LifecycleEffects.kt` (`OnResume`).

---

## 6. Sobre los campos "derivados"

El backend modela una app de **reseñas**, no de delivery, así que no envía
`priceRange`, `location`, `deliveryTime` ni `promoción`, que el diseño original del
frontend sí muestra. Para **no perder ese diseño**, esos campos puramente visuales se
derivan de forma **determinista** en `data/Mappers.kt` a partir de datos reales
(precio promedio del menú, hash del `id`, lista de promociones del endpoint
`/promotions`). Los datos sustantivos (nombre, categoría, rating, reseñas, menú,
imagen) son 100% reales.

---

## 7. Pruebas realizadas

Verificado de extremo a extremo en el emulador contra el backend local + MongoDB Atlas:

- ✅ Registro de usuario (crea cuenta + sesión JWT) y Login.
- ✅ Inicio: restaurantes destacados con **fotos reales** (Coil) y últimas reseñas.
- ✅ Top: ranking ordenado por calificación con medallas.
- ✅ Buscar: búsqueda por texto + chips de categorías reales.
- ✅ Detalle: datos + menú + reseñas; **publicar reseña** (el rating del restaurante
  se recalcula en el backend) y **marcar/quitar favorito**.
- ✅ Favoritos: lista del usuario autenticado.
- ✅ Perfil: datos de sesión, contadores y "Mis reseñas".
- ✅ Cierre de sesión y persistencia de la sesión entre reinicios.
- ✅ Editar perfil (nombre y biografía) desde "Configuración de cuenta" — persiste en el backend.
- ✅ Favoritos se actualizan al instante al volver a la pestaña (agregar/quitar).

### Limitación conocida del backend
- **"¿Olvidaste tu contraseña?"**: el backend **no** expone un endpoint de
  recuperación/restablecimiento de contraseña, así que la app solo informa que la
  función no está disponible. Para habilitarla habría que agregar ese endpoint en
  el backend (p. ej. envío de correo con token de reseteo).

---

## 8. Notas

- Convención del proyecto: identificadores en inglés; comentarios, textos de UI y
  documentación en **español**.
- DI manual mediante `ServiceLocator` (acorde al tamaño del proyecto académico).
- Para cambiar la URL del backend (p. ej. dispositivo físico), edita `BASE_URL` en
  `data/remote/ApiClient.kt`.
