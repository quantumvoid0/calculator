package com.github.quantumvoid0.calculator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class ThemeMode { LIGHT, DARK, AMOLED }

data class AppInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Int,
    val compileSdk: Int,
    val minSdk: Int,
    val targetSdk: Int,
    val buildType: String,
    val githubUrl: String = "https://github.com/quantumvoid0/calculator",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSheet(
    info: AppInfo,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SectionLabel("Theme")
            Spacer(Modifier.height(4.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                ThemeMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = themeMode == mode,
                        onClick = { onThemeChange(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index, ThemeMode.entries.size),
                        icon = {},
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector =
                                    when (mode) {
                                        ThemeMode.LIGHT -> Icons.Outlined.LightMode
                                        ThemeMode.DARK -> Icons.Outlined.DarkMode
                                        ThemeMode.AMOLED -> Icons.Outlined.Contrast
                                    },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text =
                                    when (mode) {
                                        ThemeMode.LIGHT -> "Light"
                                        ThemeMode.DARK -> "Dark"
                                        ThemeMode.AMOLED -> "AMOLED"
                                    },
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                Icon(
                    Icons.Outlined.Calculate,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        info.appName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "v${info.versionName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }

            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            SectionLabel("Application")
            InfoRow("Package name", info.packageName, monospace = true)
            InfoRow("Version name", info.versionName)
            InfoRow("Version code", info.versionCode.toString())
            InfoRow("Build type", info.buildType)

            Spacer(Modifier.height(12.dp))
            SectionLabel("SDK")
            InfoRow("Compile SDK", "API ${info.compileSdk}")
            InfoRow("Target SDK", "API ${info.targetSdk}")
            InfoRow("Min SDK", "API ${info.minSdk}")

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri(info.githubUrl) }
                        .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Source code",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            info.githubUrl.removePrefix("https://"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Icon(
                    Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = "Open",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    monospace: Boolean = false,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = if (monospace) FontFamily.Monospace else null,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
