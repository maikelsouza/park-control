package com.parkcontrol.core.ui.masks

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

val ZipCodeMaskTransformation: VisualTransformation = BrazilianZipCodeVisualTransformation()

class BrazilianZipCodeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.onlyZipCodeDigits().take(8)
        val masked = digits.toBrazilianZipCodeMask()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, digits.length)
                val transformedOffset = if (safeOffset <= 5) safeOffset else safeOffset + 1
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

fun String.onlyZipCodeDigits(): String = filter(Char::isDigit)

fun String.toBrazilianZipCodeMask(): String {
    val digits = onlyZipCodeDigits().take(8)
    return if (digits.length > 5) "${digits.substring(0, 5)}-${digits.substring(5)}" else digits
}

