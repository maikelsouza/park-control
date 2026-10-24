package com.parkcontrol.core.utils

import java.text.Normalizer
import java.util.Locale

/**
 * Utilitário para montar o payload "Pix Copia e Cola" (BR Code, padrão EMV
 * QR Code definido pelo Banco Central) a partir da chave Pix cadastrada nas
 * configurações, do valor cobrado pelo estacionamento e dos dados do
 * estabelecimento. O texto gerado pode ser transformado em QR-code (via
 * [QrCodeGenerator]) e escaneado por qualquer aplicativo bancário para
 * realizar o pagamento diretamente — sem precisar de internet ou de um
 * servidor próprio, pois todo o conteúdo é estático e local.
 */
object PixPayloadBuilder {

    /**
     * Monta o payload Pix completo (incluindo o CRC16 final).
     *
     * @param pixKey chave Pix cadastrada pelo dono do estacionamento.
     * @param amount valor a ser cobrado (ex.: valor final do ticket). Quando
     * nulo ou menor/igual a zero, o campo de valor é omitido do QR-code,
     * deixando o cliente livre para digitar o valor no app do banco.
     * @param merchantName nome do estacionamento (recebedor). Limitado a 25
     * caracteres pela especificação.
     * @param merchantCity cidade do estacionamento. Limitado a 15 caracteres.
     * @param txId identificador da transação (opcional). Quando em branco,
     * usa "***" conforme recomendado pela especificação quando não há um
     * identificador específico.
     */
    fun build(
        pixKey: String,
        amount: Double? = null,
        merchantName: String = "",
        merchantCity: String = "",
        txId: String = ""
    ): String {
        val safeMerchantName = merchantName.toPixAscii().ifBlank { "ESTACIONAMENTO" }.take(25)
        val safeMerchantCity = merchantCity.toPixAscii().ifBlank { "BRASIL" }.take(15)
        val safeTxId = txId.toPixAscii().ifBlank { "***" }.take(25)

        val merchantAccountInfo = buildString {
            append(field("00", "br.gov.bcb.pix"))
            append(field("01", pixKey.trim()))
        }

        val additionalData = field("05", safeTxId)

        val payload = buildString {
            append(field("00", "01")) // Payload Format Indicator
            append(field("26", merchantAccountInfo)) // Merchant Account Information (Pix)
            append(field("52", "0000")) // Merchant Category Code
            append(field("53", "986")) // Transaction Currency - BRL
            if (amount != null && amount > 0.0) {
                append(field("54", String.format(Locale.US, "%.2f", amount)))
            }
            append(field("58", "BR")) // Country Code
            append(field("59", safeMerchantName)) // Merchant Name
            append(field("60", safeMerchantCity)) // Merchant City
            append(field("62", additionalData)) // Additional Data Field Template
        }

        val payloadWithCrcPlaceholder = "$payload${"63"}04"
        val crc = crc16(payloadWithCrcPlaceholder)
        return "$payloadWithCrcPlaceholder$crc"
    }

    private fun field(id: String, value: String): String {
        val length = value.length.toString().padStart(2, '0')
        return "$id$length$value"
    }

    /** Remove acentos e caracteres não suportados pelo padrão Pix (ASCII). */
    private fun String.toPixAscii(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return normalized
            .replace(Regex("\\p{M}"), "")
            .uppercase(Locale.US)
            .replace(Regex("[^A-Z0-9 ]"), "")
            .trim()
    }

    /** CRC16-CCITT (polinômio 0x1021, valor inicial 0xFFFF) exigido pelo padrão Pix. */
    private fun crc16(payload: String): String {
        var crc = 0xFFFF
        for (char in payload) {
            crc = crc xor (char.code shl 8)
            repeat(8) {
                crc = if ((crc and 0x8000) != 0) {
                    (crc shl 1) xor 0x1021
                } else {
                    crc shl 1
                }
                crc = crc and 0xFFFF
            }
        }
        return crc.toString(16).uppercase(Locale.US).padStart(4, '0')
    }
}

