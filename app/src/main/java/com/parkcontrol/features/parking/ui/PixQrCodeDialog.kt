package com.parkcontrol.features.parking.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.parkcontrol.core.domain.model.ParkingLotInfo
import com.parkcontrol.core.utils.PixPayloadBuilder
import com.parkcontrol.core.utils.QrCodeGenerator
import com.parkcontrol.features.parking.domain.model.ParkingRecord

/**
 * Dialog exibido ao clicar em "Gerar QR-code Pix" após a saída do veículo
 * (quando o valor final já foi calculado). Mostra um QR-code Pix (BR Code)
 * contendo a chave Pix cadastrada nas configurações e o valor a pagar, para
 * que o dono do estacionamento apresente a tela para o cliente escanear com
 * o aplicativo do banco e efetuar o pagamento — 100% offline, sem depender
 * de nenhum servidor externo.
 */
@Composable
fun PixQrCodeDialog(
    record: ParkingRecord,
    pixKey: String,
    parkingLotInfo: ParkingLotInfo = ParkingLotInfo(),
    onDismiss: () -> Unit
) {
    val amount = record.amountPaid ?: 0.0

    val pixPayload = remember(record.id, pixKey, amount, parkingLotInfo) {
        PixPayloadBuilder.build(
            pixKey = pixKey,
            amount = amount,
            merchantName = parkingLotInfo.name,
            merchantCity = parkingLotInfo.city,
            txId = record.ticketNumber.toString()
        )
    }
    val qrBitmap = remember(pixPayload) {
        QrCodeGenerator.generate(pixPayload)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "💰 Pagamento via Pix",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Peça para o cliente escanear o QR-code abaixo com o app do banco para pagar",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR-code de pagamento Pix",
                    modifier = Modifier.size(220.dp)
                )

                Text(
                    text = "R$ %.2f".format(amount),
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                Text(
                    text = "Placa: ${record.licensePlate}",
                )
                Text(
                    text = "Chave Pix: $pixKey",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

