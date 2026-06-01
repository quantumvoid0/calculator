package com.github.quantumvoid0.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        _state.update { current -> reduce(current, action) }
    }

    private fun reduce(state: CalculatorState, action: CalculatorAction): CalculatorState {
        return when (action) {

            is CalculatorAction.Number -> {
                val newExpr = if (state.justEvaluated) action.digit
                              else state.displayExpression + action.digit
                state.copy(
                    displayExpression = newExpr,
                    displayResult = livePreview(newExpr, state.isDegrees) ?: state.displayResult,
                    justEvaluated = false, isError = false
                )
            }

            is CalculatorAction.Operator -> {
                val base = if (state.justEvaluated) state.displayResult else state.displayExpression
                val trimmed = base.trimEnd()
                val newExpr = if (trimmed.isNotEmpty() && isOperatorChar(trimmed.last())) {
                    trimmed.dropLast(1) + action.op
                } else trimmed + action.op
                state.copy(displayExpression = newExpr, justEvaluated = false, isError = false)
            }

            is CalculatorAction.ScientificFn -> {
                val base = if (state.justEvaluated) state.displayResult else state.displayExpression
                // factorial is postfix
                val newExpr = if (action.fn == "!") base + "!"
                              else base + action.fn + "("
                state.copy(displayExpression = newExpr, justEvaluated = false, isError = false)
            }

            CalculatorAction.OpenParen -> {
                val newExpr = state.displayExpression + "("
                state.copy(displayExpression = newExpr, justEvaluated = false, isError = false)
            }

            CalculatorAction.CloseParen -> {
                val newExpr = state.displayExpression + ")"
                state.copy(
                    displayExpression = newExpr,
                    displayResult = livePreview(newExpr, state.isDegrees) ?: state.displayResult,
                    justEvaluated = false, isError = false
                )
            }

            CalculatorAction.Decimal -> {
                val newExpr = if (state.justEvaluated) "0." else {
                    val expr = state.displayExpression
                    val lastOpIdx = expr.indexOfLast { isOperatorChar(it) || it == '(' }
                    val lastNum = expr.substring(if (lastOpIdx == -1) 0 else lastOpIdx + 1)
                    if ('.' in lastNum) expr
                    else if (lastNum.isEmpty()) "${expr}0."
                    else "$expr."
                }
                state.copy(displayExpression = newExpr, justEvaluated = false, isError = false)
            }

            CalculatorAction.Clear -> CalculatorState(isDegrees = state.isDegrees)

            CalculatorAction.Delete -> {
                if (state.justEvaluated) return CalculatorState(isDegrees = state.isDegrees)
                val newExpr = state.displayExpression.dropLast(1)
                state.copy(
                    displayExpression = newExpr,
                    displayResult = if (newExpr.isEmpty()) "0"
                                    else livePreview(newExpr, state.isDegrees) ?: state.displayResult,
                    isError = false
                )
            }

            CalculatorAction.Evaluate -> {
                val expr = state.displayExpression
                if (expr.isBlank()) return state
                try {
                    val result = CalculatorEngine.evaluate(expr, state.isDegrees)
                    state.copy(displayExpression = expr, displayResult = result,
                               justEvaluated = true, isError = false)
                } catch (e: Exception) {
                    state.copy(
                        displayResult = when {
                            e.message?.contains("zero", ignoreCase = true) == true -> "Can't ÷ by 0"
                            e.message?.contains("sqrt", ignoreCase = true) == true -> "Invalid input"
                            e.message?.contains("Undefined") == true -> "Undefined"
                            else -> "Error"
                        },
                        isError = true, justEvaluated = true
                    )
                }
            }

            CalculatorAction.ToggleSign -> {
                val expr = state.displayExpression
                if (expr.isBlank()) return state
                val newExpr = toggleLastSign(expr)
                state.copy(
                    displayExpression = newExpr,
                    displayResult = livePreview(newExpr, state.isDegrees) ?: state.displayResult,
                    justEvaluated = false
                )
            }

            CalculatorAction.Percent -> {
                val expr = state.displayExpression
                if (expr.isBlank()) return state
                try {
                    val value = CalculatorEngine.evaluate(expr, state.isDegrees)
                    val result = CalculatorEngine.formatResult(value.toDouble() / 100.0)
                    state.copy(displayExpression = result, displayResult = result,
                               justEvaluated = true, isError = false)
                } catch (e: Exception) { state }
            }

            CalculatorAction.ToggleDegRad -> state.copy(isDegrees = !state.isDegrees)
        }
    }

    private fun livePreview(expr: String, deg: Boolean): String? {
        if (expr.isBlank()) return null
        val trimmed = expr.trimEnd()
        if (trimmed.isEmpty() || isOperatorChar(trimmed.last()) || trimmed.last() == '(') return null
        return try { CalculatorEngine.evaluate(expr, deg) } catch (e: Exception) { null }
    }

    private fun isOperatorChar(c: Char) = c in listOf('+', '-', '*', '/', '^')

    private fun toggleLastSign(expr: String): String {
        val lastIdx = expr.indexOfLast { isOperatorChar(it) }
        return if (lastIdx == -1) {
            if (expr.startsWith("-")) expr.substring(1) else "-$expr"
        } else {
            val before = expr.substring(0, lastIdx + 1)
            val num = expr.substring(lastIdx + 1)
            if (num.startsWith("-")) "$before${num.substring(1)}" else "$before-$num"
        }
    }
}
