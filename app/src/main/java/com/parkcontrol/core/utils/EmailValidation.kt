package com.parkcontrol.core.utils

import java.util.Locale

/**
 * Regex único usado em toda a aplicação para validar endereços de e-mail.
 * Centralizado aqui para evitar duplicação entre telas/ViewModels
 * (mesmo conceito das máscaras de telefone, moeda, CEP etc).
 */
val EmailRegex: Regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

/**
 * Remove caracteres não permitidos em um e-mail enquanto o usuário digita,
 * mantendo no máximo um "@" e limitando o tamanho total.
 */
fun String.sanitizeEmailInput(): String {
    val allowedChars = buildString(length) {
        for (char in this@sanitizeEmailInput.lowercase(Locale.ROOT)) {
            if (char.isLetterOrDigit() || char in setOf('@', '.', '_', '-', '+')) append(char)
        }
    }
    val parts = allowedChars.split('@', limit = 2)
    return when {
        parts.size == 1 -> parts[0].trim()
        else -> "${parts[0].trim()}@${parts[1].trim()}"
    }.take(254)
}

/**
 * Verifica se a string tem o formato de um e-mail válido.
 */
fun String.looksLikeEmail(): Boolean {
    if (isBlank()) return false
    return EmailRegex.matches(trim())
}

