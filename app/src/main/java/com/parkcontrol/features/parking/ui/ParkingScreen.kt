package com.parkcontrol.features.parking.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkcontrol.core.navigation.AppDrawerScaffold
import com.parkcontrol.core.ui.masks.PhoneMaskTransformation
import com.parkcontrol.core.ui.masks.onlyPhoneDigits
import com.parkcontrol.core.ui.theme.ParkControlTheme
import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.ParkingStatus
import com.parkcontrol.features.parking.domain.model.formatToBrazilian
import com.parkcontrol.features.parking.domain.usecase.CalculateParkingPriceUseCase

private val SuccessGreen = Color(0xFF28A745)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingScreen(
    onNavigate: (String) -> Unit
) {
    AppDrawerScaffold(
            currentRoute = "parking",
            onNavigate = onNavigate
            )
     { paddingValues ->

         val context = LocalContext.current
         val application = context.applicationContext as android.app.Application
         val factory = ParkingViewModelFactory(
             application = application,
             calculateParkingPriceUseCase = CalculateParkingPriceUseCase()
         )
         val viewModel: ParkingViewModel = viewModel(factory = factory)

         LaunchedEffect(viewModel) {
             viewModel.onScreenOpened()
         }

         ParkingEntryScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
     }
}

@Composable
fun ParkingEntryScreen(
    viewModel: ParkingViewModel,
    modifier: Modifier = Modifier
) {

    val selectedRecord = viewModel.selectedRecord.value
    val suggestions = viewModel.openRecordSuggestions.value

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            VehiclePlateSection(viewModel)
        }

        item {
            SearchOpenRecordsSection(
                viewModel = viewModel,
                suggestions = suggestions
            )
        }

        item {
            ActionButtonsSection(viewModel, selectedRecord)
        }

        item {
            LastRecordSection(selectedRecord)
        }

        item {
            InfoSection()
        }
    }
}

@Composable
private fun VehiclePlateSection(
    viewModel: ParkingViewModel
) {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = "Placa do Veículo",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = viewModel.licensePlate.value,
        onValueChange = viewModel::updateLicensePlate,
        label = { Text("Digite a placa *") },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
        isError = viewModel.licensePlateError.value != null,
        supportingText = {
            viewModel.licensePlateError.value?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = viewModel.phone.value,
        onValueChange = { typedPhone ->
            viewModel.updatePhone(typedPhone.onlyPhoneDigits().take(11))
        },
        label = { Text("Telefone") },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = PhoneMaskTransformation,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(
                width = 3.dp,
                color = colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(40.dp)
                        .background(
                            colorScheme.primary,
                            RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🇧🇷", fontSize = 24.sp)
                }

                Text(
                    text = viewModel.licensePlate.value
                        .ifEmpty { "ABC1D23" },
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "BR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        colorScheme.surfaceVariant,
                        RoundedCornerShape(2.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun ActionButtonsSection(
    viewModel: ParkingViewModel,
    selectedRecord: ParkingRecord?
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Button(
            onClick = viewModel::registerEntry,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "🚗 ENTRADA",
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimary
            )
        }

        Button(
            onClick = viewModel::registerLastExit,
            enabled = selectedRecord?.status == ParkingStatus.ESTACIONADO,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SuccessGreen
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "🚗 SAÍDA",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SearchOpenRecordsSection(
    viewModel: ParkingViewModel,
    suggestions: List<ParkingRecord>
) {
    val colorScheme = MaterialTheme.colorScheme
    val hasQuery = viewModel.licensePlate.value.isNotBlank() ||
        viewModel.phone.value.isNotBlank()

    if (!hasQuery) {
        return
    }

    Text(
        text = "Veículos sem saída registrada",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (suggestions.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Nenhum veiculo estacionado encontrado",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions, key = { it.id }) { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectSuggestedRecord(record) },
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "🚗 ${record.licensePlate}",
                        fontWeight = FontWeight.Bold
                    )
                    if (record.phone.isNotBlank()) {
                        Text(
                            text = "📞 ${record.phone}",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "Entrada: ${record.entryTime.formatToBrazilian()}",
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LastRecordSection(
    record: ParkingRecord?
) {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = "Registro selecionado",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (record == null) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Nenhum registro selecionado",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "🚗 ${record.licensePlate}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (record.phone.isNotBlank()) {
                RecordInfo("Telefone", record.phone)
            }

            HorizontalDivider()

            RecordInfo("Entrada", record.entryTime.formatToBrazilian())

            record.exitTime?.let {
                RecordInfo("Saída", it.formatToBrazilian())
            }

            record.amountPaid?.let {
                RecordInfo("Valor", "R$ %.2f".format(it))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Status",
                    color = colorScheme.onSurfaceVariant
                )

                val parked = record.status == ParkingStatus.ESTACIONADO

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (parked) colorScheme.primaryContainer else colorScheme.secondaryContainer
                ) {
                    Text(
                        text = record.status.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (parked) colorScheme.onPrimaryContainer else colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordInfo(
    label: String,
    value: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        Text(
            text = "$label:",
            fontSize = 12.sp,
            color = colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InfoSection() {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = "ℹ️ Digite placa ou telefone para buscar os veículos sem saída registrada.\nSelecione um registro para dar saída.",
        fontSize = 12.sp,
        color = colorScheme.onPrimaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorScheme.primaryContainer,
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ParkingScreenPreview() {
    ParkControlTheme {
        ParkingScreen(
            onNavigate = {}
        )
    }
}