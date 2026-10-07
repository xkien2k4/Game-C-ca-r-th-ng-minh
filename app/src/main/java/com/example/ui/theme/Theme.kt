package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SoftOceanColorScheme = lightColorScheme(
    primary = SoftOceanBlueDark,
    onPrimary = WhitePure,
    primaryContainer = SoftOceanBlueLight,
    onPrimaryContainer = TextBlackPure,
    secondary = SoftOceanBluePrimary,
    onSecondary = TextBlackPure,
    secondaryContainer = SoftOceanBlueLightest,
    onSecondaryContainer = TextBlackPure,
    tertiary = PieceRedX,
    onTertiary = WhitePure,
    background = WhiteSurface,
    onBackground = TextBlackPure,
    surface = WhitePure,
    onSurface = TextBlackPure,
    surfaceVariant = SoftOceanBlueLightest,
    onSurfaceVariant = TextBlackSecondary,
    outline = SoftOceanBorder,
    outlineVariant = WhiteBorder
)

@Composable
fun CaroMasterTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SoftOceanColorScheme,
        typography = Typography,
        content = content
    )
}
