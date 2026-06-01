package com.github.quantumvoid0.calculator

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }

    private fun loadTheme(): ThemeMode =
        ThemeMode.valueOf(prefs.getString("theme", ThemeMode.DARK.name) ?: ThemeMode.DARK.name)

    private fun saveTheme(mode: ThemeMode) =
        prefs.edit().putString("theme", mode.name).apply()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val info = AppInfo(
            appName     = getString(R.string.app_name),
            packageName = packageName,
            versionName = packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0",
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                              packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
                          else
                              @Suppress("DEPRECATION")
                              packageManager.getPackageInfo(packageName, 0).versionCode,
            compileSdk  = applicationInfo.targetSdkVersion,
            minSdk      = applicationInfo.minSdkVersion,
            targetSdk   = applicationInfo.targetSdkVersion,
            buildType   = if (BuildConfig.DEBUG) "debug" else "release",
        )

        setContent {
            var themeMode by mutableStateOf(loadTheme())

            CalculatorTheme(themeMode = themeMode) {
                CalculatorScreen(
                    appInfo       = info,
                    themeMode     = themeMode,
                    onThemeChange = { themeMode = it; saveTheme(it) },
                )
            }
        }
    }
}
