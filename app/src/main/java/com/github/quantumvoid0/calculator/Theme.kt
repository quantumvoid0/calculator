package com.github.quantumvoid0.calculator

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()
private val AmoledColors =
    darkColorScheme(
        background = Color.Black,
        surface = Color.Black,
        surfaceVariant = Color(0xFF1A1A1A),
        surfaceContainerHigh = Color(0xFF1C1C1C),
        surfaceContainerLow = Color(0xFF0D0D0D),
        surfaceContainer = Color(0xFF141414),
    )

@Composable
fun CalculatorTheme(
    themeMode: ThemeMode = if (isSystemInDarkTheme()) ThemeMode.DARK else ThemeMode.LIGHT,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when (themeMode) {
            ThemeMode.AMOLED -> {
                AmoledColors
            }

            ThemeMode.LIGHT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dynamicLightColorScheme(LocalContext.current)
                } else {
                    LightColors
                }
            }

            ThemeMode.DARK -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dynamicDarkColorScheme(LocalContext.current)
                } else {
                    DarkColors
                }
            }
        }

    val isDark = themeMode != ThemeMode.LIGHT
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content,
    )
}
