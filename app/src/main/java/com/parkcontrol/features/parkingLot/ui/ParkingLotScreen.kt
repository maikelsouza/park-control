package com.parkcontrol.features.parkingLot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkcontrol.core.navigation.AppDrawerScaffold
import com.parkcontrol.core.navigation.AppRoutes
import com.parkcontrol.core.ui.common.BrazilianStates
import com.parkcontrol.core.ui.common.requiredFieldError
import com.parkcontrol.core.ui.masks.PhoneMaskTransformation
import com.parkcontrol.core.ui.masks.ZipCodeMaskTransformation


@Composable
fun ParkingLotScreen(
    onNavigate: (String) -> Unit,
    onSaved: (String) -> Unit = {}
) {
    AppDrawerScaffold(
        currentRoute = AppRoutes.ParkingLotRegistration.route,
        onNavigate = onNavigate
    ) { paddingValues ->
        val viewModel: ParkingLotViewModel = viewModel()
        ParkingLotEntryScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues),
            onSaved = onSaved
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingLotEntryScreen(
    viewModel: ParkingLotViewModel,
    modifier: Modifier = Modifier,
    onSaved: (String) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    var stateExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val streetError = requiredFieldError(viewModel.street, viewModel.showValidation)
    val neighborhoodError = requiredFieldError(viewModel.neighborhood, viewModel.showValidation)
    val cityError = requiredFieldError(viewModel.city, viewModel.showValidation)
    val stateError = requiredFieldError(viewModel.state, viewModel.showValidation)
    val zipCodeError = requiredFieldError(viewModel.zipCode, viewModel.showValidation)

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Estacionamento",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Informe os dados do seu estacionamento",
            color = colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = viewModel.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = viewModel.phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Telefone") },
            placeholder = { Text("(00) 00000-0000") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            visualTransformation = PhoneMaskTransformation
        )

        OutlinedTextField(
            value = viewModel.street,
            onValueChange = viewModel::onStreetChange,
            label = { Text("Rua *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            isError = streetError != null,
            supportingText = {
                if (streetError != null) {
                    Text(streetError, color = colorScheme.error)
                } else if (viewModel.street.length >= ParkingLotViewModel.MAX_STREET_LENGTH) {
                    Text("Limite de ${ParkingLotViewModel.MAX_STREET_LENGTH} caracteres atingido")
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = viewModel.number,
                onValueChange = viewModel::onNumberChange,
                label = { Text("Número") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    if (viewModel.number.length >= ParkingLotViewModel.MAX_NUMBER_LENGTH) {
                        Text("Limite de ${ParkingLotViewModel.MAX_NUMBER_LENGTH} caracteres atingido")
                    }
                }
            )
            OutlinedTextField(
                value = viewModel.complement,
                onValueChange = viewModel::onComplementChange,
                label = { Text("Complemento") },
                placeholder = { Text("Galpão, fundos, próximo a...") },
                modifier = Modifier.weight(2f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                supportingText = {
                    if (viewModel.complement.length >= ParkingLotViewModel.MAX_COMPLEMENT_LENGTH) {
                        Text("Limite de ${ParkingLotViewModel.MAX_COMPLEMENT_LENGTH} caracteres atingido")
                    }
                }
            )
        }

        OutlinedTextField(
            value = viewModel.neighborhood,
            onValueChange = viewModel::onNeighborhoodChange,
            label = { Text("Bairro *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            isError = neighborhoodError != null,
            supportingText = {
                if (neighborhoodError != null) {
                    Text(neighborhoodError, color = colorScheme.error)
                } else if (viewModel.neighborhood.length >= ParkingLotViewModel.MAX_NEIGHBORHOOD_LENGTH) {
                    Text("Limite de ${ParkingLotViewModel.MAX_NEIGHBORHOOD_LENGTH} caracteres atingido")
                }
            }
        )

        OutlinedTextField(
            value = viewModel.city,
            onValueChange = viewModel::onCityChange,
            label = { Text("Cidade *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            isError = cityError != null,
            supportingText = {
                if (cityError != null) {
                    Text(cityError, color = colorScheme.error)
                } else if (viewModel.city.length >= ParkingLotViewModel.MAX_CITY_LENGTH) {
                    Text("Limite de ${ParkingLotViewModel.MAX_CITY_LENGTH} caracteres atingido")
                }
            }
        )

        ExposedDropdownMenuBox(
            expanded = stateExpanded,
            onExpandedChange = { stateExpanded = !stateExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewModel.state,
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado *") },
                placeholder = { Text("UF") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                singleLine = true,
                isError = stateError != null,
                supportingText = {
                    if (stateError != null) Text(stateError, color = colorScheme.error)
                }
            )
            ExposedDropdownMenu(
                expanded = stateExpanded,
                onDismissRequest = { stateExpanded = false }
            ) {
                BrazilianStates.forEach { uf ->
                    DropdownMenuItem(
                        text = { Text(uf) },
                        onClick = {
                            viewModel.onStateChange(uf)
                            stateExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = viewModel.zipCode,
            onValueChange = viewModel::onZipCodeChange,
            label = { Text("CEP *") },
            placeholder = { Text("00000-000") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = ZipCodeMaskTransformation,
            isError = zipCodeError != null,
            supportingText = {
                if (zipCodeError != null) Text(zipCodeError, color = colorScheme.error)
            }
        )

        Button(
            onClick = {
                viewModel.saveParkingLot {
                    onSaved("Estacionamento salvo com sucesso")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !viewModel.isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            )
        ) {
            Icon(
                Icons.Default.Save,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Salvar",
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimary
            )
        }

        SnackbarHost(hostState = snackbarHostState)

        Spacer(modifier = Modifier.height(8.dp))
    }
}



