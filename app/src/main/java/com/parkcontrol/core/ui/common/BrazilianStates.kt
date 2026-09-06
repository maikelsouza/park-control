package com.parkcontrol.core.ui.common

/**
 * List of Brazilian state abbreviations (UFs), shared across forms that
 * collect an address (e.g., Agreement registration, Parking Lot registration).
 */
val BrazilianStates = listOf(
    "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO",
    "MA", "MG", "MS", "MT", "PA", "PB", "PE", "PI", "PR",
    "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO"
)

/**
 * Shared "required field" validation message used by form screens that
 * collect address data (e.g., Agreement registration, Parking Lot registration).
 * Returns "Campo obrigatório" when [showValidation] is true and [value] is blank.
 */
fun requiredFieldError(value: String, showValidation: Boolean): String? {
    return if (showValidation && value.isBlank()) "Campo obrigatório" else null
}

