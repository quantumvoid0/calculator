package com.github.quantumvoid0.calculator

data class CalculatorState(
    val displayExpression: String = "",
    val displayResult: String = "0",
    val isError: Boolean = false,
    val justEvaluated: Boolean = false,
    val isDegrees: Boolean = true,
)
