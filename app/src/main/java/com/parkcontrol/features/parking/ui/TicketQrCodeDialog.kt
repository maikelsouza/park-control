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
import com.parkcontrol.core.utils.QrCodeGenerator
import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.formatAsTicketNumber
import com.parkcontrol.features.parking.domain.model.formatToTicketDisplay
import com.parkcontrol.features.parking.domain.model.toTicketMessage

/**
 * Dialog exibido ao clicar em "Gerar QR-Code" na tela de entrada, representando
 * o ticket digital de estacionamento: um QR-code com o texto puro do ticket
 * (placa, entrada e número do ticket). Ao ser escaneado com a câmera/app de
 * QR-code do celular do cliente, o texto é exibido diretamente na tela —
 * 100% offline, sem depender de internet, de servidor externo ou de qualquer
 * app específico instalado. O cliente pode copiar o texto ou tirar um print
 * da tela para guardar o ticket no próprio celular.
 */
@Composable
fun TicketQrCodeDialog(
    record: ParkingRecord,
    onDismiss: () -> Unit
) {
    val ticketMessage = remember(record.id, record.ticketNumber) {
        record.toTicketMessage()
    }
    val qrBitmap = remember(ticketMessage) {
        QrCodeGenerator.generate(ticketMessage)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🎫 Ticket Digital",
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
                    text = "Peça para o cliente escanear o QR-code abaixo com a câmera do celular",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR-code do ticket de estacionamento",
                    modifier = Modifier.size(220.dp)
                )

                Text(
                    text = "Ao escanear, o texto do ticket aparece direto na tela " +
                        "(sem internet). O cliente pode copiar ou tirar um print " +
                        "para guardar no celular.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Text(
                    text = "Ticket ${record.ticketNumber.formatAsTicketNumber()}",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Placa: ${record.licensePlate}")
                Text(text = "Entrada: ${record.entryTime.formatToTicketDisplay()}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}


