package com.parkcontrol.features.parking.domain.model

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
 * É texto puro (sem link/URL), então funciona 100% offline: a câmera/app de
 * QR-code do cliente decodifica e exibe o texto diretamente na tela, sem
 * precisar de internet nem de nenhum app específico instalado. O cliente pode
 * copiar o texto ou tirar um print para guardar o ticket no celular.
 *
 * Quando o estacionamento tiver nome e/ou telefone cadastrados (tela de
 * configuração do estacionamento), esses dados são incluídos no início do
 * ticket para identificar o local e permitir contato.
 */
fun ParkingRecord.toTicketMessage(
    parkingLotName: String = "",
    parkingLotPhone: String = ""
): String {
    return buildString {
        appendLine("🚗 Ticket de Estacionamento")
        if (parkingLotName.isNotBlank()) {
            appendLine(parkingLotName.trim())
        }
        if (parkingLotPhone.isNotBlank()) {
            appendLine("Tel: ${parkingLotPhone.formatPhoneForDisplay()}")
        }
        appendLine()
        appendLine("Placa: $licensePlate")
        appendLine("Entrada: ${entryTime.formatToTicketDisplay()}")
        append("Ticket: ${ticketNumber.formatAsTicketNumber()}")
    }
}

/**
 * Formata um telefone contendo apenas dígitos no padrão brasileiro
 * (00) 00000-0000 ou (00) 0000-0000, para exibição no ticket digital.
 * Caso não se encaixe nos formatos esperados, retorna o valor original.
 */
fun String.formatPhoneForDisplay(): String {
    val digits = filter(Char::isDigit)
    return when (digits.length) {
        11 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}"
        10 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 6)}-${digits.substring(6)}"
        else -> this
    }
}


