package com.parkcontrol.core.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Utilitário simples para gerar QR-codes localmente (sem servidor),
 * usado para criar o ticket digital de estacionamento.
 */
object QrCodeGenerator {

    /**
     * Gera um [Bitmap] em preto e branco representando o QR-code do [content] informado.
     *
     * @param content texto que será codificado dentro do QR-code.
     * @param sizePx largura/altura (em pixels) do bitmap resultante (imagem quadrada).
     */
    fun generate(content: String, sizePx: Int = 512): Bitmap {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1
        )

        val bitMatrix = QRCodeWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            sizePx,
            sizePx,
            hints
        )

        val bitmap = createBitmap(sizePx, sizePx, Bitmap.Config.RGB_565)
        for (x in 0 until sizePx) {
            for (y in 0 until sizePx) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    }
}



