package dev.atick.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors()

private val LightColorPalette = lightColors(
    primary = PrimaryLight,
    primaryVariant = PrimaryVariantLight,
    secondary = SecondaryLight,
    secondaryVariant = SecondaryVariantLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = ErrorLight,
    onPrimary = OnPrimaryLight,
    onSecondary = OnSecondaryLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    onError = OnErrorLight
)

@Composable
fun ComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content,
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes
    )
}