package com.parkcontrol.core.ui.masks

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

private val ptBrLocale = Locale.Builder().setLanguage("pt").setRegion("BR").build()

val CurrencyMaskTransformation: VisualTransformation = BrazilianCurrencyVisualTransformation()

class BrazilianCurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.onlyMoneyDigits().take(11)
        val masked = digits.toBrazilianCurrencyMask()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, digits.length)
                if (safeOffset == 0) return 0
                var digitCount = 0
                masked.forEachIndexed { index, char ->
                    if (char.isDigit()) {
                        digitCount++
                        if (digitCount == safeOffset) return index + 1
                    }
                }
                return masked.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, masked.length)
                return masked.take(safeOffset).count(Char::isDigit).coerceAtMost(digits.length)
            }
        }

        return TransformedText(AnnotatedString(masked), offsetMapping)
    }
}

/** Keeps only the digits typed by the user, representing the value in cents. */
fun String.onlyMoneyDigits(): String = filter(Char::isDigit)

/** Formats a digits-only string (cents) as a Brazilian currency mask, e.g. "R$ 12,34". */
fun String.toBrazilianCurrencyMask(): String {
    val digits = onlyMoneyDigits().take(11)
    if (digits.isEmpty()) return ""
    val cents = digits.toLongOrNull() ?: return ""
    val integerPart = cents / 100
    val decimalPart = (cents % 100).toString().padStart(2, '0')
    val integerFormatted = NumberFormat.getIntegerInstance(ptBrLocale).format(integerPart)
    return "R$ $integerFormatted,$decimalPart"
}

/** Converts a digits-only string (cents) into its decimal value, e.g. "1234" -> 12.34 */
fun String.moneyDigitsToDoubleOrNull(): Double? {
    val digits = onlyMoneyDigits()
    if (digits.isEmpty()) return null
    val cents = digits.toLongOrNull() ?: return null
    return cents / 100.0
}

