package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalCyberAccent = staticCompositionLocalOf { NeonCrimson }

@Composable
fun NexaVortexTheme(
    accentColor: Color = NeonCrimson,
    content: @Composable () -> Unit
) {
    val darkColorScheme = darkColorScheme(
        primary = accentColor,
        onPrimary = Color.Black,
        primaryContainer = accentColor.copy(alpha = 0.2f),
        onPrimaryContainer = TextWhite,
        secondary = accentColor.copy(alpha = 0.8f),
        onSecondary = Color.Black,
        background = CyberBackground,
        onBackground = TextWhite,
        surface = CyberSurface,
        onSurface = TextWhite,
        surfaceVariant = CyberCard,
        onSurfaceVariant = TextGray,
        outline = CyberBorder,
        outlineVariant = CyberBorderGlow
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CyberBackground.toArgb()
                window.navigationBarColor = CyberBackground.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(LocalCyberAccent provides accentColor) {
        MaterialTheme(
            colorScheme = darkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
