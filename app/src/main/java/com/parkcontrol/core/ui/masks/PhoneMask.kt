package com.parkcontrol.core.ui.masks

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

val PhoneMaskTransformation: VisualTransformation = BrazilianPhoneVisualTransformation()

class BrazilianPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.onlyPhoneDigits().take(11)
        val masked = digits.toBrazilianPhoneMask()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, digits.length)
                val transformedOffset = when {
                    safeOffset == 0 -> 0
                    safeOffset <= 2 -> safeOffset + 1
                    digits.length <= 10 && safeOffset <= 6 -> safeOffset + 3
                    else -> safeOffset + 4
                }
                return transformedOffset.coerceAtMost(masked.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, masked.length)
                return masked.take(safeOffset).count(Char::isDigit).coerceAtMost(digits.length)
            }
        }

        return TransformedText(AnnotatedString(masked), offsetMapping)
    }
}

fun String.onlyPhoneDigits(): String = filter(Char::isDigit)

fun String.toBrazilianPhoneMask(): String {
    val digits = onlyPhoneDigits().take(11)
    return when {
        digits.isEmpty() -> ""
        digits.length <= 2 -> "(${digits}"
        digits.length <= 6 -> "(${digits.substring(0, 2)}) ${digits.substring(2)}"
        digits.length <= 10 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 6)}-${digits.substring(6)}"
        else -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}"
    }
}
