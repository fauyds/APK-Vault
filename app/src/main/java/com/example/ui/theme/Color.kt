package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Expressive M3 Color definitions as specified:
// Light Theme Background: #E6E6E6
val LightAppBackground = Color(0xFFE6E6E6)
val DarkAppBackground = Color(0xFF262626)

val LightCardSurface = Color(0xFFFFFFFF)
val DarkCardSurface = Color(0xFF404040)

// Fallback Monet base palette colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

data class ExpressiveColors(
    val appBackground: Color,
    val cardSurface: Color,
    val nestedElementSurface: Color,
    val buttonPrimary: Color,
    val buttonTertiarySubtle: Color,
    val selectorSelected: Color,
    val selectorUnselected: Color,
    val onAppBackground: Color,
    val onCardSurface: Color,
    val onNestedElementSurface: Color,
    val onButtonPrimary: Color,
    val onButtonTertiarySubtle: Color,
    val onSelectorSelected: Color,
    val onSelectorUnselected: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val successColor: Color = Color(0xFF16A34A),
    val errorColor: Color = Color(0xFFDC2626),
    val warningColor: Color = Color(0xFFD97706)
)

val LocalExpressiveColors = staticCompositionLocalOf<ExpressiveColors> {
    error("No ExpressiveColors provided")
}

object ExpressiveTheme {
    val colors: ExpressiveColors
        @Composable
        get() = LocalExpressiveColors.current
}
