package com.example.foodboxd.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary              = YellowPrimary,
    onPrimary            = Black,
    primaryContainer     = YellowContainer,
    onPrimaryContainer   = Neutral900,

    secondary            = Neutral700,
    onSecondary          = White,
    secondaryContainer   = Neutral150,
    onSecondaryContainer = Neutral900,

    tertiary             = Neutral600,
    onTertiary           = White,
    tertiaryContainer    = Neutral200,
    onTertiaryContainer  = Neutral800,

    background           = White,
    onBackground         = Neutral900,

    surface              = Neutral100,
    onSurface            = Neutral900,
    surfaceVariant       = Neutral150,
    onSurfaceVariant     = Neutral600,

    outline              = Neutral300,
    outlineVariant       = Neutral200,

    error                = ErrorLight,
    onError              = White,
    errorContainer       = ErrorContainerLight,
    onErrorContainer     = ErrorOnContainer,

    scrim                = Black,
)

private val DarkColorScheme = darkColorScheme(
    primary              = YellowPrimary,
    onPrimary            = Black,
    primaryContainer     = YellowDark,
    onPrimaryContainer   = YellowOnDark,

    secondary            = Neutral300,
    onSecondary          = Neutral900,
    secondaryContainer   = Neutral800,
    onSecondaryContainer = Neutral150,

    tertiary             = Neutral400,
    onTertiary           = Neutral900,
    tertiaryContainer    = DarkSurfaceVariant,
    onTertiaryContainer  = Neutral200,

    background           = DarkBackground,
    onBackground         = White,

    surface              = DarkSurface,
    onSurface            = Neutral150,
    surfaceVariant       = DarkSurfaceVariant,
    onSurfaceVariant     = Neutral400,

    outline              = Neutral600,
    outlineVariant       = Neutral800,

    error                = ErrorDark,
    onError              = Black,
    errorContainer       = ErrorContainerDark,
    onErrorContainer     = ErrorOnContainerDk,

    scrim                = Black,
)

@Composable
fun FoodboxdTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
