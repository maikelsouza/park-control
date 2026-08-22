package com.parkcontrol.features.parking.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkcontrol.core.navigation.AppDrawerScaffold
import com.parkcontrol.core.navigation.AppRoutes
import com.parkcontrol.core.ui.masks.formatPlateInput
import com.parkcontrol.core.ui.masks.plateInputPlaceholder
import com.parkcontrol.features.monthlyCustomers.domain.model.PlateType
import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.ParkingStatus
import com.parkcontrol.features.parking.domain.model.formatToBrazilian
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val statusFilter by viewModel.statusFilter
    val records by viewModel.filteredRecords
    val filterError by viewModel.filterError
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var plateTypeFilter by remember { mutableStateOf(PlateType.MERCOSUL) }

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

        Text(
            text = "Tipo de Placa",
            style = MaterialTheme.typography.labelMedium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PlateType.entries.forEach { type ->
                FilterChip(
                    selected = plateTypeFilter == type,
                    onClick = {
                        plateTypeFilter = type
                        viewModel.updatePlateFilter("")
                    },
                    label = { Text(type.displayName) }
                )
            }
        }

        OutlinedTextField(
            value = plateFilter,
            onValueChange = { typed -> viewModel.updatePlateFilter(formatPlateInput(typed, plateTypeFilter)) },
            label = { Text("Placa") },
            placeholder = { Text(plateInputPlaceholder(plateTypeFilter)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DateFilterField(
                value = startDateFilter,
                label = "Data inicial",
                onClick = { showStartDatePicker = true },
                modifier = Modifier.weight(1f)
            )

            DateFilterField(
                value = endDateFilter,
                label = "Data final",
                onClick = { showEndDatePicker = true },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = statusFilter == null,
                onClick = { viewModel.updateStatusFilter(null) },
                label = { Text("Todos") }
            )
            FilterChip(
                selected = statusFilter == ParkingStatus.ESTACIONADO,
                onClick = { viewModel.updateStatusFilter(ParkingStatus.ESTACIONADO) },
                label = { Text(ParkingStatus.ESTACIONADO.label) }
            )
            FilterChip(
                selected = statusFilter == ParkingStatus.FINALIZADO,
                onClick = { viewModel.updateStatusFilter(ParkingStatus.FINALIZADO) },
                label = { Text(ParkingStatus.FINALIZADO.label) }
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

        if (showStartDatePicker) {
            val startDatePickerState = rememberDatePickerState(
                initialSelectedDateMillis = startDateFilter.toDatePickerMillisOrNull()
            )
            PtBrDatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                onConfirm = {
                    startDatePickerState.selectedDateMillis
                        ?.toBrazilianDateOrNull()
                        ?.let(viewModel::updateStartDateFilter)
                    showStartDatePicker = false
                },
                onDismiss = { showStartDatePicker = false }
            ) {
                DatePicker(state = startDatePickerState)
            }
        }

        if (showEndDatePicker) {
            val endDatePickerState = rememberDatePickerState(
                initialSelectedDateMillis = endDateFilter.toDatePickerMillisOrNull()
            )
            PtBrDatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                onConfirm = {
                    endDatePickerState.selectedDateMillis
                        ?.toBrazilianDateOrNull()
                        ?.let(viewModel::updateEndDateFilter)
                    showEndDatePicker = false
                },
                onDismiss = { showEndDatePicker = false }
            ) {
                DatePicker(state = endDatePickerState)
            }
        }
    }
}

@Composable
private fun DateFilterField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            placeholder = { Text("Selecione") },
            singleLine = true,
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Overlay transparente que captura os cliques antes do TextField
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        )
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

            record.amountPaid?.let { paid ->
                val discount = record.discountAmount ?: 0.0
                val grossAmount = paid + discount

                if (discount > 0.0) {
                    Text(
                        text = "Valor estacionamento: R$ %.2f".format(grossAmount),
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Desconto: - R$ %.2f".format(discount),
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Valor pago: R$ %.2f".format(paid),
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Valor: R$ %.2f".format(paid),
                        color = colorScheme.onSurfaceVariant
                    )
                }
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

private fun String.toDatePickerMillisOrNull(): Long? {
    if (isBlank()) return null
    return runCatching {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDate = java.time.LocalDate.parse(trim(), formatter)
        localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }.getOrNull()
}

private fun Long.toBrazilianDateOrNull(): String? {
    return runCatching {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        Instant.ofEpochMilli(this)
            .atOffset(ZoneOffset.UTC)
            .toLocalDate()
            .format(formatter)
    }.getOrNull()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PtBrDatePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val ptBr = Locale.forLanguageTag("pt-BR")
    val ptBrConfig = Configuration(LocalConfiguration.current).apply { setLocale(ptBr) }

    CompositionLocalProvider(LocalConfiguration provides ptBrConfig) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onConfirm) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        ) {
            content()
        }
    }
}



