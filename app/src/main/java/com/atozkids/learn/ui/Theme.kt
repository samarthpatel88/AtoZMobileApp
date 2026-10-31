package com.atozkids.learn.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.atozkids.learn.R

val Coral = Color(0xFFFF6B6B)
val Sky = Color(0xFF4D96FF)
val Sun = Color(0xFFFFB703)
val Mint = Color(0xFF2FB463)
val Grape = Color(0xFF9B5DE5)
val Orange = Color(0xFFFF8E3C)
val Pink = Color(0xFFF15BB5)
val Teal = Color(0xFF00B4B6)
val Cream = Color(0xFFFFF9EE)
val Ink = Color(0xFF2B2D42)
val Muted = Color(0xFF8A8CA0)

/** One colour per category, in the order they appear. */
val CategoryColors = listOf(Coral, Sky, Teal, Orange, Pink, Grape, Mint, Sun)

val Baloo = FontFamily(
    Font(R.font.baloo2_medium, FontWeight.Medium),
    Font(R.font.baloo2_bold, FontWeight.Bold),
    Font(R.font.baloo2_extrabold, FontWeight.ExtraBold),
)

private val Base = Typography()
private fun TextStyle.kid(weight: FontWeight = FontWeight.Medium) = copy(fontFamily = Baloo, fontWeight = weight)

private val KidTypography = Typography(
    displayLarge = Base.displayLarge.kid(FontWeight.ExtraBold),
    displayMedium = Base.displayMedium.kid(FontWeight.ExtraBold),
    displaySmall = Base.displaySmall.kid(FontWeight.ExtraBold),
    headlineLarge = Base.headlineLarge.kid(FontWeight.ExtraBold),
    headlineMedium = Base.headlineMedium.kid(FontWeight.ExtraBold),
    headlineSmall = Base.headlineSmall.kid(FontWeight.Bold),
    titleLarge = Base.titleLarge.kid(FontWeight.ExtraBold),
    titleMedium = Base.titleMedium.kid(FontWeight.Bold),
    titleSmall = Base.titleSmall.kid(FontWeight.Bold),
    bodyLarge = Base.bodyLarge.kid(),
    bodyMedium = Base.bodyMedium.kid(),
    bodySmall = Base.bodySmall.kid(),
    labelLarge = Base.labelLarge.kid(FontWeight.Bold),
    labelMedium = Base.labelMedium.kid(FontWeight.Bold),
    labelSmall = Base.labelSmall.kid(FontWeight.Bold),
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
