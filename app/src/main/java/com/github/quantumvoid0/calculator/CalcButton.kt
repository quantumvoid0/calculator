package com.github.quantumvoid0.calculator

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonType {
    Number,
    Operator,
    Function,
    Equals,
    Scientific,
}

@Composable
private fun buttonColors(type: ButtonType) =
    when (type) {
        ButtonType.Number -> {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        ButtonType.Operator -> {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }

        ButtonType.Function -> {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }

        ButtonType.Scientific -> {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
            )
        }

        ButtonType.Equals -> {
            ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }

@Composable
fun CalcButton(
    label: String,
    type: ButtonType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(50)
    val colors = buttonColors(type)
    val fs: TextUnit =
        when {
            label.length >= 5 -> 12.sp
            label.length == 4 -> 14.sp
            label.length == 3 -> 16.sp
            label.length == 2 -> 20.sp
            else -> 26.sp
        }

    if (type == ButtonType.Equals) {
        ElevatedButton(
            onClick = onClick,
            modifier = modifier.aspectRatio(1f).fillMaxWidth(),
            shape = shape,
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp),
            colors = colors,
            contentPadding =
                androidx.compose.foundation.layout
                    .PaddingValues(0.dp),
        ) {
            Text(
                text = label,
                fontSize = fs,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                softWrap = false,
            )
        }
    } else {
        FilledTonalButton(
            onClick = onClick,
            modifier = modifier.aspectRatio(1f).fillMaxWidth(),
            shape = shape,
            colors = colors,
            contentPadding =
                androidx.compose.foundation.layout
                    .PaddingValues(0.dp),
        ) {
            Text(
                text = label,
                fontSize = fs,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                softWrap = false,
                overflow = TextOverflow.Clip,
            )
        }
    }
}

// /flat button used for the scientific strip and the bottom numpad row
// / font size is computed from label length so superscript labels like "sin⁻¹" stay readable
@Composable
fun CalcButtonFlat(
    label: String,
    type: ButtonType,
    modifier: Modifier = Modifier,
    fontSize: TextUnit =
        when {
            label.length >= 6 -> 10.sp
            label.length == 5 -> 11.sp
            label.length == 4 -> 13.sp
            label.length == 3 -> 15.sp
            label.length == 2 -> 18.sp
            else -> 22.sp
        },
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(50)
    val colors = buttonColors(type)

    if (type == ButtonType.Equals) {
        ElevatedButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp),
            colors = colors,
            contentPadding =
                androidx.compose.foundation.layout
                    .PaddingValues(horizontal = 4.dp, vertical = 0.dp),
        ) {
            Text(
                text = label,
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                softWrap = false,
            )
        }
    } else {
        FilledTonalButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = colors,
            contentPadding =
                androidx.compose.foundation.layout
                    .PaddingValues(horizontal = 4.dp, vertical = 0.dp),
        ) {
            Text(
                text = label,
                fontSize = fontSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                softWrap = false,
                overflow = TextOverflow.Visible,
                maxLines = 1,
            )
        }
    }
}
