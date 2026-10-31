package com.atozkids.learn.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

val Coral = Color(0xFFFF6B6B)
val Sky = Color(0xFF4D96FF)
val Sun = Color(0xFFFFB703)
val Mint = Color(0xFF3DBE6C)
val Grape = Color(0xFF9B5DE5)
val Orange = Color(0xFFFF8E3C)
val Pink = Color(0xFFF15BB5)
val Teal = Color(0xFF00B4B6)
val Cream = Color(0xFFFFF8E7)
val Ink = Color(0xFF2B2D42)

/** One colour per category, in the order they appear. */
val CategoryColors = listOf(Coral, Sky, Teal, Orange, Pink, Grape, Mint, Sun)

private val Base = Typography()
private val KidTypography = Typography(
    displayLarge = Base.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
    displayMedium = Base.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
    headlineLarge = Base.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
    headlineMedium = Base.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
    headlineSmall = Base.headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.Bold),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.Bold),
)

// Kids' app: always the bright light theme.
@Composable
fun AtoZTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Coral,
            onPrimary = Color.White,
            secondary = Sky,
            tertiary = Mint,
            background = Cream,
            onBackground = Ink,
            surface = Color.White,
            onSurface = Ink,
        ),
        typography = KidTypography,
        content = content,
    )
}
