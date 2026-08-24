package com.parkcontrol.core.ui.masks

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.parkcontrol.features.monthlyCustomers.domain.model.PlateType

/** Formats the raw typing into the appropriate plate mask, based on the selected [PlateType]. */
fun formatPlateInput(input: String, plateType: PlateType): String {
    return when (plateType) {
        PlateType.MERCOSUL -> {
            // LLLNLNN — positions: 0-2 letter, 3 digit, 4 letter, 5-6 digit
            val clean = input.filter { it.isLetterOrDigit() }.uppercase()
            buildString {
                for (i in clean.indices) {
                    if (length >= 7) break
                    val ch = clean[i]
                    when (length) {
                        0, 1, 2 -> if (ch.isLetter()) append(ch)
                        3 -> if (ch.isDigit()) append(ch)
                        4 -> if (ch.isLetter()) append(ch)
                        5, 6 -> if (ch.isDigit()) append(ch)
                    }
                }
            }
        }
        PlateType.OUTRA -> {
            // LLL-NNNN — positions: 0-2 letter, 3-6 digit (order enforced, digits before the 3 letters are ignored)
            val clean = input.filter { it.isLetterOrDigit() }.uppercase()
            val core = buildString {
                for (ch in clean) {
                    if (length >= 7) break
                    when (length) {
                        0, 1, 2 -> if (ch.isLetter()) append(ch)
                        else -> if (ch.isDigit()) append(ch)
                    }
                }
            }
            if (core.length <= 3) core else "${core.take(3)}-${core.substring(3)}"
        }
    }
}

/**
 * Formats a [TextFieldValue] using [formatPlateInput], keeping the cursor in the correct
 * position even when mask characters (like the hyphen on the old plate format) are
 * inserted/removed. Without this, letting the plain-String overload of the mask reformat
 * the text makes Compose's default diffing move the cursor back one position whenever the
 * hyphen is added right before the digits.
 */
fun formatPlateInputValue(newValue: TextFieldValue, plateType: PlateType): TextFieldValue {
    val typed = newValue.text
    val cursorPosition = newValue.selection.end.coerceIn(0, typed.length)
    val alnumBeforeCursor = typed.take(cursorPosition).count { it.isLetterOrDigit() }
    val formatted = formatPlateInput(typed, plateType)

    var alnumCount = 0
    var newCursor = formatted.length
    for (i in formatted.indices) {
        if (alnumCount == alnumBeforeCursor) {
            newCursor = i
            break
        }
        if (formatted[i].isLetterOrDigit()) alnumCount++
    }
    // Skip past any mask separator (e.g. the hyphen) right after the matched position,
    // so the cursor lands ready for the next digit instead of right before the hyphen.
    while (newCursor < formatted.length && !formatted[newCursor].isLetterOrDigit()) {
        newCursor++
    }

    return TextFieldValue(text = formatted, selection = TextRange(newCursor))
}

/** Placeholder text to show for the plate input, based on the selected [PlateType]. */
fun plateInputPlaceholder(plateType: PlateType): String {
    return if (plateType == PlateType.MERCOSUL) "ABC1D23" else "ABC-1234"
}

