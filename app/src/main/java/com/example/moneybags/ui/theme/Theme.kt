package com.example.moneybags.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val DarkColorScheme = darkColorScheme(
    primary = PaleGreen,
    onPrimary = DeepBlue,
    secondary = SoftNeutral,
    onSecondary = DeepBlue,
    tertiary = FreshGreen,
    onTertiary = DeepBlue,
    background = DeepBlue,
    onBackground = SoftNeutral,
    surface = DeepBlue,
    onSurface = SoftNeutral,
    surfaceVariant = Teal,
    onSurfaceVariant = SoftNeutral,
    surfaceContainer = DeepBlue,
    surfaceContainerHigh = DeepBlue,
    surfaceContainerHighest = DeepBlue,
    secondaryContainer = FreshGreen,
    onSecondaryContainer = DeepBlue
)

private val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    onPrimary = SoftNeutral,
    secondary = Teal,
    onSecondary = SoftNeutral,
    tertiary = DeepBlue,
    onTertiary = SoftNeutral,
    background = SoftNeutral,
    onBackground = DeepBlue,
    surface = SoftNeutral,
    onSurface = DeepBlue,
    surfaceVariant = Teal,
    onSurfaceVariant = SoftNeutral,
    surfaceContainer = Teal,
    secondaryContainer = FreshGreen,
    onSecondaryContainer = DeepBlue,
    tertiaryContainer = PaleGreen,
    onTertiaryContainer = DeepBlue
)

@Composable
fun MoneyBagsTheme(
    darkTheme: Boolean = false,
    textScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val systemDensity = LocalDensity.current

    CompositionLocalProvider(
        LocalDensity provides Density(systemDensity.density, systemDensity.fontScale * textScale)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
