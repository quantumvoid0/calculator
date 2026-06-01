package com.github.quantumvoid0.calculator

sealed class CalculatorAction {
    data class Number(
        val digit: String,
    ) : CalculatorAction()

    data class Operator(
        val op: String,
    ) : CalculatorAction()

    data class ScientificFn(
        val fn: String,
    ) : CalculatorAction()

    object Decimal : CalculatorAction()

    object Clear : CalculatorAction()

    object Delete : CalculatorAction()

    object Evaluate : CalculatorAction()

    object ToggleSign : CalculatorAction()

    object Percent : CalculatorAction()

    object ToggleDegRad : CalculatorAction()

    object OpenParen : CalculatorAction()

    object CloseParen : CalculatorAction()
}
