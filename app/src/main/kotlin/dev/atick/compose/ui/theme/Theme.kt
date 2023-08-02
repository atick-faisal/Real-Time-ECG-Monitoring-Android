package dev.atick.compose.ui.theme

import ai.atick.material.MaterialColor
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = PrimaryVariant,
    primaryVariant = Primary,
    secondary = PrimaryVariant,
    secondaryVariant = Primary,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = MaterialColor.Amber700,
    onPrimary = MaterialColor.White,
    onSecondary = MaterialColor.White,
    onBackground = MaterialColor.BlueGray50,
    onSurface = MaterialColor.BlueGray50,
    onError = MaterialColor.White
)

private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Primary,
    secondaryVariant = PrimaryVariant,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = MaterialColor.Amber50,
    onPrimary = MaterialColor.White,
    onSecondary = MaterialColor.White,
    onBackground = MaterialColor.BlueGray800,
    onSurface = MaterialColor.BlueGray800,
    onError = MaterialColor.White
)

@Composable
fun ComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        shapes = Shapes,
        content = content,
    )
}