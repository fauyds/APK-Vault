package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val BaseDarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkAppBackground,
    surface = DarkCardSurface
)

private val BaseLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = LightAppBackground,
    surface = LightCardSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val baseMonetScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> BaseDarkColorScheme
        else -> BaseLightColorScheme
    }

    // Material 3 Expressive UI tokens
    val appBg = if (darkTheme) DarkAppBackground else LightAppBackground
    val cardSurf = if (darkTheme) DarkCardSurface else LightCardSurface
    val nestedElement = appBg
    val btnPrimary = baseMonetScheme.primary
    val btnTertiarySubtle = baseMonetScheme.tertiaryContainer.copy(alpha = 0.85f)
    val selSelected = btnPrimary
    val selUnselected = cardSurf

    val textPrimary = if (darkTheme) Color(0xFFF3F4F6) else Color(0xFF111827)
    val textSecondary = if (darkTheme) Color(0xFF9CA3AF) else Color(0xFF4B5563)
    val textMuted = if (darkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF)

    val expressiveColors = ExpressiveColors(
        appBackground = appBg,
        cardSurface = cardSurf,
        nestedElementSurface = nestedElement,
        buttonPrimary = btnPrimary,
        buttonTertiarySubtle = btnTertiarySubtle,
        selectorSelected = selSelected,
        selectorUnselected = selUnselected,
        onAppBackground = textPrimary,
        onCardSurface = textPrimary,
        onNestedElementSurface = textPrimary,
        onButtonPrimary = baseMonetScheme.onPrimary,
        onButtonTertiarySubtle = baseMonetScheme.onTertiaryContainer,
        onSelectorSelected = baseMonetScheme.onPrimary,
        onSelectorUnselected = textPrimary,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textMuted = textMuted
    )

    val appliedColorScheme = baseMonetScheme.copy(
        background = appBg,
        surface = cardSurf,
        onBackground = textPrimary,
        onSurface = textPrimary,
        primary = btnPrimary,
        onPrimary = baseMonetScheme.onPrimary
    )

    CompositionLocalProvider(LocalExpressiveColors provides expressiveColors) {
        MaterialTheme(
            colorScheme = appliedColorScheme,
            typography = Typography,
            content = content
        )
    }
}
