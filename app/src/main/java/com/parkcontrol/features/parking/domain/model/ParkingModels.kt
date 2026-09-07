package com.parkcontrol.features.parking.domain.model

import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class ParkingRecord(
    val id: String = UUID.randomUUID().toString(),
    val customerId: Int? = null,
    val licensePlate: String,
    val phone: String = "",
    val entryTime: LocalDateTime,
    val exitTime: LocalDateTime? = null,
    val status: ParkingStatus = ParkingStatus.ESTACIONADO,
    val amountPaid: Double? = null,
    val discountAmount: Double? = null,
    val isManualDiscount: Boolean = false,
    // Número sequencial exibido ao cliente no ticket digital (QR-code).
    // É gerado automaticamente pelo banco de dados no momento da entrada.
    val ticketNumber: Long = 0
)

fun LocalDateTime.formatToBrazilian(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return this.format(formatter)
}

/**
 * Formata a data/hora de entrada no padrão usado no ticket digital,
 * ex: "07/09/2026 às 14:35".
 */
fun LocalDateTime.formatToTicketDisplay(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")
    return this.format(formatter)
}

/**
 * Formata o número do ticket com zeros à esquerda, ex: "#00042".
 */
fun Long.formatAsTicketNumber(): String {
    return "#%05d".format(this)
}

/**
 * Monta o conteúdo textual do ticket digital de estacionamento.
 * Esse texto é o que fica codificado dentro do QR-code gerado na entrada.
 */
fun ParkingRecord.toTicketMessage(): String {
    return buildString {
        appendLine("🚗 Ticket de Estacionamento")
        appendLine()
        appendLine("Placa: $licensePlate")
        appendLine("Entrada: ${entryTime.formatToTicketDisplay()}")
        append("Ticket: ${ticketNumber.formatAsTicketNumber()}")
    }
}

/**
 * URL base da página estática (hospedada via GitHub Pages, sem backend próprio)
 * que exibe o ticket digital como um cartão visual e permite salvá-lo como
 * imagem no celular do cliente. Ver `docs/ticket/index.html` no repositório.
 */
private const val TICKET_PAGE_BASE_URL = "https://maikelsouza.github.io/park-control/ticket/"

/**
 * Monta a URL da página do ticket digital com os dados do registro codificados
 * como query params. Esse é o conteúdo real codificado dentro do QR-code: ao
 * escanear com a câmera do celular, o navegador abre uma página que mostra o
 * ticket como um cartão visual, com um botão para salvar a imagem diretamente
 * no celular do cliente (galeria/downloads) — sem depender do WhatsApp estar
 * instalado, e reconhecível por qualquer leitor de QR-code (link https comum).
 */
fun ParkingRecord.toTicketPageUrl(): String {
    fun encode(value: String) = URLEncoder.encode(value, "UTF-8")
    val placa = encode(licensePlate)
    val entrada = encode(entryTime.formatToTicketDisplay())
    val ticket = encode(ticketNumber.formatAsTicketNumber())
    return "$TICKET_PAGE_BASE_URL?placa=$placa&entrada=$entrada&ticket=$ticket"
}

