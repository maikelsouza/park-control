package com.parkcontrol.core.ui.masks

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

/** Placeholder text to show for the plate input, based on the selected [PlateType]. */
fun plateInputPlaceholder(plateType: PlateType): String {
    return if (plateType == PlateType.MERCOSUL) "ABC1D23" else "ABC-1234"
}

