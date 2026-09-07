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
 * Monta o link "click to chat" do WhatsApp (https://wa.me) contendo o texto do
 * ticket digital já pré-preenchido. Esse é o conteúdo real codificado dentro do
 * QR-code: ao escanear com a câmera do celular, o sistema abre o WhatsApp (ou,
 * caso não esteja instalado, o navegador) com a mensagem do ticket pronta para
 * ser enviada. Não depende de nenhuma página/servidor externo — o link em si
 * só é resolvido pelo próprio WhatsApp já instalado no celular do cliente.
 *
 * O destinatário é o próprio telefone informado pelo cliente no momento da
 * entrada ([ParkingRecord.phone]) — ou seja, ao escanear com o celular dele,
 * o WhatsApp abre a conversa/nota para esse mesmo número, servindo como um
 * comprovante digital que fica salvo no próprio WhatsApp do cliente. Quando o
 * telefone não foi informado na entrada, o link abre o WhatsApp sem
 * destinatário definido, e o próprio cliente escolhe para qual contato enviar
 * (por exemplo, o próprio contato "Você").
 */
fun ParkingRecord.toWhatsAppTicketLink(): String {
    val encodedMessage = URLEncoder.encode(toTicketMessage(), "UTF-8")
        .replace("+", "%20")
    val phoneDigits = phone.filter(Char::isDigit)
    return if (phoneDigits.isNotBlank()) {
        "https://wa.me/$phoneDigits?text=$encodedMessage"
    } else {
        "https://wa.me/?text=$encodedMessage"
    }
}

