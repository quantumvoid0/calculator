package com.github.quantumvoid0.calculator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val SCI_BTN = 72.dp
private val SCI_GAP = 6.dp

@Composable
fun CalculatorScreen(
    appInfo: AppInfo,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    viewModel: CalculatorViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showAbout by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
        ) {
            DisplayPanel(
                state = state,
                onInfoClick = { showAbout = true },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            ScientificStrip(
                isDegrees = state.isDegrees,
                onAction = viewModel::onAction,
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            StandardGrid(
                onAction = viewModel::onAction,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }

    if (showAbout) {
        AboutSheet(
            info = appInfo,
            themeMode = themeMode,
            onThemeChange = onThemeChange,
            onDismiss = { showAbout = false },
        )
    }
}

// display
@Composable
private fun DisplayPanel(
    state: CalculatorState,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 16.dp),
    ) {
        // (i)
        IconButton(
            onClick = onInfoClick,
            modifier = Modifier.align(Alignment.TopEnd).size(36.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "About",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp),
            )
        }

        // expression + result pinned to bottom end
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize(),
        ) {
            AnimatedContent(
                targetState = state.displayExpression,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "expression",
            ) { expr ->
                Text(
                    text = expr.ifEmpty { "" },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            AnimatedContent(
                targetState = state.displayResult,
                transitionSpec = {
                    (slideInVertically { it / 2 } + fadeIn()) togetherWith
                        (slideOutVertically { -it / 2 } + fadeOut())
                },
                label = "result",
            ) { result ->
                val textSize =
                    when {
                        result.length > 14 -> 32.sp
                        result.length > 9 -> 48.sp
                        else -> 64.sp
                    }
                Text(
                    text = result,
                    fontSize = textSize,
                    fontWeight = FontWeight.Light,
                    color =
                        if (state.isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// scientific strip horiz
@Composable
private fun ScientificStrip(
    isDegrees: Boolean,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Row(
        modifier =
            modifier
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(SCI_GAP),
    ) {
        // Deg Rad toggle
        CalcButtonFlat(
            label = if (isDegrees) "DEG" else "RAD",
            type = ButtonType.Function,
            modifier = Modifier.width(SCI_BTN).height(SCI_BTN),
            fontSize = 12.sp,
        ) { onAction(CalculatorAction.ToggleDegRad) }

        // Trig
        SciBtn("sin") { onAction(CalculatorAction.ScientificFn("sin")) }
        SciBtn("cos") { onAction(CalculatorAction.ScientificFn("cos")) }
        SciBtn("tan") { onAction(CalculatorAction.ScientificFn("tan")) }
        SciBtn("sec") { onAction(CalculatorAction.ScientificFn("sec")) }
        SciBtn("csc") { onAction(CalculatorAction.ScientificFn("csc")) }
        SciBtn("cot") { onAction(CalculatorAction.ScientificFn("cot")) }

        // Inverse trig
        SciBtn("sin⁻¹") { onAction(CalculatorAction.ScientificFn("arcsin")) }
        SciBtn("cos⁻¹") { onAction(CalculatorAction.ScientificFn("arccos")) }
        SciBtn("tan⁻¹") { onAction(CalculatorAction.ScientificFn("arctan")) }
        SciBtn("sec⁻¹") { onAction(CalculatorAction.ScientificFn("arcsec")) }
        SciBtn("csc⁻¹") { onAction(CalculatorAction.ScientificFn("arccsc")) }
        SciBtn("cot⁻¹") { onAction(CalculatorAction.ScientificFn("arccot")) }

        // Powers&roots
        SciBtn("√x") { onAction(CalculatorAction.ScientificFn("sqrt")) }
        SciBtn("x²") {
            onAction(CalculatorAction.Operator("^"))
            onAction(CalculatorAction.Number("2"))
        }
        SciBtn("xʸ") { onAction(CalculatorAction.Operator("^")) }
        SciBtn("n!") { onAction(CalculatorAction.ScientificFn("!")) }

        // Logarithms&exponentials
        SciBtn("log") { onAction(CalculatorAction.ScientificFn("log")) }
        SciBtn("ln") { onAction(CalculatorAction.ScientificFn("ln")) }
        SciBtn("eˣ") { onAction(CalculatorAction.ScientificFn("exp")) }
        SciBtn("10ˣ") {
            onAction(CalculatorAction.Number("10"))
            onAction(CalculatorAction.Operator("^"))
        }

        // Constants
        SciBtn("π") { onAction(CalculatorAction.Number("pi")) }
        SciBtn("e") { onAction(CalculatorAction.Number("e")) }

        // Brackets
        SciBtn("(") { onAction(CalculatorAction.OpenParen) }
        SciBtn(")") { onAction(CalculatorAction.CloseParen) }

        // Percent
        SciBtn("%") { onAction(CalculatorAction.Percent) }
    }
}

@Composable
private fun SciBtn(
    label: String,
    onClick: () -> Unit,
) {
    CalcButtonFlat(
        label = label,
        type = ButtonType.Scientific,
        modifier = Modifier.width(SCI_BTN).height(SCI_BTN),
        onClick = onClick,
    )
}

// standard strip vert
@Composable
private fun StandardGrid(
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcButton("AC", ButtonType.Function, Modifier.weight(1f)) { onAction(CalculatorAction.Clear) }
            CalcButton("+/−", ButtonType.Function, Modifier.weight(1f)) { onAction(CalculatorAction.ToggleSign) }
            CalcButton("%", ButtonType.Function, Modifier.weight(1f)) { onAction(CalculatorAction.Percent) }
            CalcButton("÷", ButtonType.Operator, Modifier.weight(1f)) { onAction(CalculatorAction.Operator("/")) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcButton("7", ButtonType.Number, Modifier.weight(1f)) { onAction(CalculatorAction.Number("7")) }
            CalcButton("8", ButtonType.Number, Modifier.weight(1f)) { onAction(CalculatorAction.Number("8")) }
            CalcButton("9", ButtonType.Number, Modifier.weight(1f)) { onAction(CalculatorAction.Number("9")) }
            CalcButton("×", ButtonType.Operator, Modifier.weight(1f)) { onAction(CalculatorAction.Operator("*")) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcButton("4", ButtonType.Number, Modifier.weight(1f)) { onAction(CalculatorAction.Number("4")) }
            CalcButton("5", ButtonType.Number, Modifier.weight(1f)) { onAction(CalculatorAction.Number("5")) }
            CalcButton("6", ButtonType.Number, Modifier.weight(1f)) { onAction(CalculatorAction.Number("6")) }
            CalcButton("−", ButtonType.Operator, Modifier.weight(1f)) { onAction(CalculatorAction.Operator("-")) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcButton("1", ButtonType.Number, Modifier.weight(1f)) { onAction(CalculatorAction.Number("1")) }
            CalcButton("2", ButtonType.Number, Modifier.weight(1f)) { onAction(CalculatorAction.Number("2")) }
            CalcButton("3", ButtonType.Number, Modifier.weight(1f)) { onAction(CalculatorAction.Number("3")) }
            CalcButton("+", ButtonType.Operator, Modifier.weight(1f)) { onAction(CalculatorAction.Operator("+")) }
        }
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CalcButtonFlat("0", ButtonType.Number, Modifier.weight(1f).aspectRatio(1f)) { onAction(CalculatorAction.Number("0")) }
            CalcButtonFlat(".", ButtonType.Number, Modifier.weight(1f).aspectRatio(1f)) { onAction(CalculatorAction.Decimal) }
            CalcButtonFlat("⌫", ButtonType.Function, Modifier.weight(1f).aspectRatio(1f)) { onAction(CalculatorAction.Delete) }
            CalcButtonFlat("=", ButtonType.Equals, Modifier.weight(1f).aspectRatio(1f)) { onAction(CalculatorAction.Evaluate) }
        }
    }
}
