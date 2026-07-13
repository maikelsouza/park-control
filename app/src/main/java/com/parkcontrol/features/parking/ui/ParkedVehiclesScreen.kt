package com.parkcontrol.features.parking.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkcontrol.core.navigation.AppDrawerScaffold
import com.parkcontrol.core.navigation.AppRoutes
import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.ParkingStatus
import com.parkcontrol.features.parking.domain.model.formatToBrazilian
import java.time.Duration
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkedVehiclesScreen(
    onNavigate: (String) -> Unit
) {
    AppDrawerScaffold(
        currentRoute = AppRoutes.ParkedVehicles.route,
        onNavigate = onNavigate
    ) { paddingValues ->
        val context = LocalContext.current
        val application = context.applicationContext as android.app.Application
        val factory = ParkedVehiclesViewModelFactory(application = application)
        val viewModel: ParkedVehiclesViewModel = viewModel(factory = factory)

        ParkedVehiclesContent(
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ParkedVehiclesContent(
    viewModel: ParkedVehiclesViewModel,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val plateFilter by viewModel.plateFilter
    val startDateFilter by viewModel.startDateFilter
    val endDateFilter by viewModel.endDateFilter
    val records by viewModel.filteredRecords
    val filterError by viewModel.filterError

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Listagem de veiculos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Exibe os que estao estacionados e os que ja finalizaram",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = plateFilter,
            onValueChange = viewModel::updatePlateFilter,
            label = { Text("Placa") },
            placeholder = { Text("Ex: ABC1D23") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = startDateFilter,
                onValueChange = viewModel::updateStartDateFilter,
                label = { Text("Data inicial") },
                placeholder = { Text("dd/MM/yyyy") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = endDateFilter,
                onValueChange = viewModel::updateEndDateFilter,
                label = { Text("Data final") },
                placeholder = { Text("dd/MM/yyyy") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = viewModel::applyFilters,
                modifier = Modifier.weight(1f)
            ) {
                Text("Filtrar")
            }

            Button(
                onClick = viewModel::clearFilters,
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpar")
            }
        }

        filterError?.let { message ->
            Text(
                text = message,
                color = colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = "Encontrados: ${records.size}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum registro encontrado")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(records, key = { it.id }) { record ->
                    ParkedVehicleCard(record = record)
                }
            }
        }
    }
}

@Composable
private fun ParkedVehicleCard(record: ParkingRecord) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Placa: ${record.licensePlate}",
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Text(
                text = "Entrada: ${record.entryTime.formatToBrazilian()}",
                color = colorScheme.onSurfaceVariant
            )

            val statusLabel = if (record.status == ParkingStatus.ESTACIONADO) {
                "Estacionado"
            } else {
                "Finalizado"
            }
            Text(
                text = "Status: $statusLabel",
                color = colorScheme.onSurfaceVariant
            )

            record.exitTime?.let {
                Text(
                    text = "Saida: ${it.formatToBrazilian()}",
                    color = colorScheme.onSurfaceVariant
                )
            }

            record.amountPaid?.let {
                Text(
                    text = "Valor: R$ %.2f".format(it),
                    color = colorScheme.onSurfaceVariant
                )
            }

            if (record.phone.isNotBlank()) {
                Text(
                    text = "Telefone: ${record.phone}",
                    color = colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Tempo estacionado:",
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDuration(record.entryTime),
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun formatDuration(entryTime: LocalDateTime): String {
    val duration = Duration.between(entryTime, LocalDateTime.now())
    val totalMinutes = duration.toMinutes().coerceAtLeast(0)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return "%02dh %02dmin".format(hours, minutes)
}



