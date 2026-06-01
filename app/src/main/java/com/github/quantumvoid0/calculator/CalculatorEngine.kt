package com.github.quantumvoid0.calculator

import kotlin.math.*

object CalculatorEngine {
    fun evaluate(
        expression: String,
        isDegrees: Boolean = true,
    ): String {
        val tokens = tokenize(expression.trim())
        val result = parseExpr(tokens, 0, isDegrees).first
        return formatResult(result)
    }

    // tokenss
    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isWhitespace() -> {
                    i++
                }

                c.isDigit() || c == '.' -> {
                    val start = i
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) i++
                    tokens.add(expr.substring(start, i))
                }

                c.isLetter() -> {
                    val start = i
                    while (i < expr.length && expr[i].isLetter()) i++
                    tokens.add(expr.substring(start, i))
                }

                c == '(' -> {
                    tokens.add("(")
                    i++
                }

                c == ')' -> {
                    tokens.add(")")
                    i++
                }

                c == '!' -> {
                    tokens.add("!")
                    i++
                }

                c in listOf('+', '-', '*', '/', '^') -> {
                    tokens.add(c.toString())
                    i++
                }

                else -> {
                    i++
                }
            }
        }
        return tokens
    }

    // parser
    private fun parseExpr(
        tokens: List<String>,
        index: Int,
        deg: Boolean,
    ): Pair<Double, Int> = parseAddSub(tokens, index, deg)

    private fun parseAddSub(
        tokens: List<String>,
        index: Int,
        deg: Boolean,
    ): Pair<Double, Int> {
        var (left, pos) = parseMulDiv(tokens, index, deg)
        while (pos < tokens.size && tokens[pos] in listOf("+", "-")) {
            val op = tokens[pos]
            val (right, newPos) = parseMulDiv(tokens, pos + 1, deg)
            left = if (op == "+") left + right else left - right
            pos = newPos
        }
        return left to pos
    }

    private fun parseMulDiv(
        tokens: List<String>,
        index: Int,
        deg: Boolean,
    ): Pair<Double, Int> {
        var (left, pos) = parsePower(tokens, index, deg)
        while (pos < tokens.size && tokens[pos] in listOf("*", "/")) {
            val op = tokens[pos]
            val (right, newPos) = parsePower(tokens, pos + 1, deg)
            if (op == "/" && right == 0.0) throw ArithmeticException("Division by zero")
            left = if (op == "*") left * right else left / right
            pos = newPos
        }
        return left to pos
    }

    private fun parsePower(
        tokens: List<String>,
        index: Int,
        deg: Boolean,
    ): Pair<Double, Int> {
        var (base, pos) = parseUnary(tokens, index, deg)
        if (pos < tokens.size && tokens[pos] == "^") {
            val (exp, newPos) = parsePower(tokens, pos + 1, deg) // right assoc
            base = base.pow(exp)
            pos = newPos
        }
        return base to pos
    }

    private fun parseUnary(
        tokens: List<String>,
        index: Int,
        deg: Boolean,
    ): Pair<Double, Int> {
        if (index < tokens.size && tokens[index] == "-") {
            val (value, pos) = parseFactorial(tokens, index + 1, deg)
            return -value to pos
        }
        return parseFactorial(tokens, index, deg)
    }

    private fun parseFactorial(
        tokens: List<String>,
        index: Int,
        deg: Boolean,
    ): Pair<Double, Int> {
        var (value, pos) = parsePrimary(tokens, index, deg)
        while (pos < tokens.size && tokens[pos] == "!") {
            value = factorial(value)
            pos++
        }
        return value to pos
    }

    private fun parsePrimary(
        tokens: List<String>,
        index: Int,
        deg: Boolean,
    ): Pair<Double, Int> {
        if (index >= tokens.size) throw IllegalArgumentException("Unexpected end")
        val token = tokens[index]

        // number literal
        token.toDoubleOrNull()?.let { return it to (index + 1) }

        // constants
        if (token == "pi" || token == "π") return PI to (index + 1)
        if (token == "e") return E to (index + 1)

        // parenthesised sub expression
        if (token == "(") {
            val (value, pos) = parseExpr(tokens, index + 1, deg)
            val closePos = if (pos < tokens.size && tokens[pos] == ")") pos + 1 else pos
            return value to closePos
        }

        // func expecting a parenthesised argument
        val fnNames =
            setOf(
                "sqrt",
                "sin",
                "cos",
                "tan",
                "sec",
                "csc",
                "cot",
                "arcsin",
                "arccos",
                "arctan",
                "arcsec",
                "arccsc",
                "arccot",
                "log",
                "ln",
                "exp",
            )
        if (token in fnNames) {
            val argIndex =
                if (index + 1 < tokens.size && tokens[index + 1] == "(") {
                    index + 2
                } else {
                    index + 1
                }
            val (arg, pos) = parseExpr(tokens, argIndex, deg)
            val closePos = if (pos < tokens.size && tokens[pos] == ")") pos + 1 else pos
            val result = applyFn(token, arg, deg)
            return result to closePos
        }

        throw IllegalArgumentException("Unknown token: $token")
    }

    private fun applyFn(
        fn: String,
        x: Double,
        deg: Boolean,
    ): Double {
        val rad = if (deg) Math.toRadians(x) else x
        return when (fn) {
            "sqrt" -> {
                if (x < 0) throw ArithmeticException("sqrt of negative")
                sqrt(x)
            }

            "sin" -> {
                sin(rad)
            }

            "cos" -> {
                cos(rad)
            }

            "tan" -> {
                tan(rad)
            }

            "sec" -> {
                val c = cos(rad)
                if (abs(c) < 1e-12) throw ArithmeticException("Undefined")
                1.0 / c
            }

            "csc" -> {
                val s = sin(rad)
                if (abs(s) < 1e-12) throw ArithmeticException("Undefined")
                1.0 / s
            }

            "cot" -> {
                val s = sin(rad)
                if (abs(s) < 1e-12) throw ArithmeticException("Undefined")
                cos(rad) / s
            }

            "arcsin" -> {
                if (deg) Math.toDegrees(asin(x)) else asin(x)
            }

            "arccos" -> {
                if (deg) Math.toDegrees(acos(x)) else acos(x)
            }

            "arctan" -> {
                if (deg) Math.toDegrees(atan(x)) else atan(x)
            }

            "arcsec" -> {
                if (abs(x) < 1.0) throw ArithmeticException("Undefined")
                val v = acos(1.0 / x)
                if (deg) Math.toDegrees(v) else v
            }

            "arccsc" -> {
                if (abs(x) < 1.0) throw ArithmeticException("Undefined")
                val v = asin(1.0 / x)
                if (deg) Math.toDegrees(v) else v
            }

            "arccot" -> {
                val v = atan(1.0 / x)
                if (deg) Math.toDegrees(v) else v
            }

            "log" -> {
                if (x <= 0) throw ArithmeticException("log of non-positive")
                log10(x)
            }

            "ln" -> {
                if (x <= 0) throw ArithmeticException("ln of non-positive")
                ln(x)
            }

            "exp" -> {
                exp(x)
            }

            else -> {
                throw IllegalArgumentException("Unknown function: $fn")
            }
        }
    }

    private fun factorial(n: Double): Double {
        if (n < 0 || n != kotlin.math.floor(n)) throw ArithmeticException("Factorial undefined")
        if (n > 20) throw ArithmeticException("Factorial too large")
        var result = 1.0
        for (i in 2..n.toInt()) result *= i
        return result
    }

    fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) throw ArithmeticException("Result is undefined")
        // round away floating-point noise
        // sin(180deg) ≈ 1.2e-16 =  0 and sf it
        val rounded = BigDecimalRound(value)
        return if (rounded == kotlin.math.floor(rounded) && !rounded.isInfinite()) {
            rounded.toLong().toString()
        } else {
            "%.10f".format(rounded).trimEnd('0').trimEnd('.')
        }
    }

    private fun BigDecimalRound(value: Double): Double = "%.10f".format(value).toDouble()
}
