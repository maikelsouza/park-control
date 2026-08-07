package com.parkcontrol.features.monthlyCustomers.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkcontrol.features.monthlyCustomers.domain.model.PlateType
import com.parkcontrol.features.monthlyCustomers.domain.model.VehicleCategory
import java.util.Locale

private val CommonBrands = listOf(
    "Audi", "BMW", "BYD", "Caoa Chery", "Chevrolet", "Citroën", "Fiat", "Ford",
    "Honda", "Hyundai", "Jeep", "Kia", "Land Rover", "Mercedes-Benz", "Mitsubishi",
    "Nissan", "Peugeot", "Renault", "Toyota", "Volkswagen", "Volvo", "Outro"
)

private val CommonColors = listOf(
    "Amarelo", "Azul", "Bege", "Branco", "Cinza", "Laranja",
    "Marrom", "Prata", "Preto", "Roxo", "Verde", "Vermelho", "Outro"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerVehicleFormScreen(
    customerId: Int,
    vehicleId: Int?,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application
    val factory = CustomerVehicleViewModelFactory(application)
    val viewModel: CustomerVehicleViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Form state
    var brand by rememberSaveable(vehicleId) { mutableStateOf("") }
    var model by rememberSaveable(vehicleId) { mutableStateOf("") }
    var color by rememberSaveable(vehicleId) { mutableStateOf("") }
    var plate by rememberSaveable(vehicleId) { mutableStateOf("") }
    var plateType by rememberSaveable(vehicleId) { mutableStateOf(PlateType.MERCOSUL) }
    var category by rememberSaveable(vehicleId) { mutableStateOf(VehicleCategory.OUTRO) }
    var parkingSpot by rememberSaveable(vehicleId) { mutableStateOf("") }
    var didPrefill by rememberSaveable(vehicleId) { mutableStateOf(false) }

    // Dropdown expanded states
    var brandExpanded by remember { mutableStateOf(false) }
    var colorExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(vehicleId) {
        viewModel.loadVehicleForEdit(vehicleId)
    }

    LaunchedEffect(vehicleId) {
        viewModel.loadForCustomer(customerId)
    }

    LaunchedEffect(uiState.selectedVehicle, vehicleId) {
        val v = uiState.selectedVehicle
        if (vehicleId != null && v != null && !didPrefill) {
            brand = v.brand
            model = v.model
            color = v.color
            plate = v.plate
            plateType = v.plateType
            category = v.category
            parkingSpot = v.parkingSpot.orEmpty()
            didPrefill = true
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            viewModel.clearSuccessMessage()
            if (uiState.saveAndAddAnother) {
                // Reset form for a new vehicle
                brand = ""
                model = ""
                color = ""
                plate = ""
                plateType = PlateType.MERCOSUL
                category = VehicleCategory.OUTRO
                parkingSpot = ""
                didPrefill = false
                snackbarHostState.showSnackbar(it)
            } else {
                onFinish()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (vehicleId == null) "Novo Veículo" else "Editar Veículo")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Marca ──────────────────────────────────────────────────
                ExposedDropdownMenuBox(
                    expanded = brandExpanded,
                    onExpandedChange = { brandExpanded = !brandExpanded }
                ) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Marca") },
                        placeholder = { Text("Fiat, Volkswagen…") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                            .fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    val filtered = CommonBrands.filter {
                        brand.isBlank() || it.contains(brand, ignoreCase = true)
                    }
                    if (filtered.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = brandExpanded,
                            onDismissRequest = { brandExpanded = false }
                        ) {
                            filtered.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        brand = option
                                        brandExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // ── Modelo ─────────────────────────────────────────────────
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Modelo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                // ── Cor ────────────────────────────────────────────────────
                ExposedDropdownMenuBox(
                    expanded = colorExpanded,
                    onExpandedChange = { colorExpanded = !colorExpanded }
                ) {
                    OutlinedTextField(
                        value = color,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cor") },
                        placeholder = { Text("Selecione") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorExpanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth(),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = colorExpanded,
                        onDismissRequest = { colorExpanded = false }
                    ) {
                        CommonColors.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { color = option; colorExpanded = false }
                            )
                        }
                    }
                }

                // ── Tipo de Placa ──────────────────────────────────────────
                Text("Tipo de Placa", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlateType.entries.forEach { type ->
                        FilterChip(
                            selected = plateType == type,
                            onClick = {
                                plateType = type
                                plate = ""   // reset plate when type changes
                            },
                            label = { Text(type.displayName) }
                        )
                    }
                }

                // ── Placa ──────────────────────────────────────────────────
                OutlinedTextField(
                    value = plate,
                    onValueChange = { typed -> plate = formatPlateInput(typed, plateType) },
                    label = { Text("Placa *") },
                    placeholder = { Text(if (plateType == PlateType.MERCOSUL) "ABC1D23" else "ABC-1234") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrect = false
                    ),
                    supportingText = { Text(plateType.displayName) }
                )

                // ── Categoria ──────────────────────────────────────────────
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth(),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        VehicleCategory.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.displayName) },
                                onClick = { category = option; categoryExpanded = false }
                            )
                        }
                    }
                }

                // ── Vaga ───────────────────────────────────────────────────
                OutlinedTextField(
                    value = parkingSpot,
                    onValueChange = { parkingSpot = it.uppercase(Locale.ROOT) },
                    label = { Text("Vaga (opcional)") },
                    placeholder = { Text("Ex: A-12, 101") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                )
            }

            // ── Botões de ação ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                if (vehicleId == null) {
                    OutlinedButton(
                        onClick = {
                            viewModel.saveVehicle(
                                customerId = customerId,
                                vehicleId = null,
                                brand = brand,
                                model = model,
                                color = color,
                                plate = plate,
                                plateType = plateType,
                                category = category,
                                parkingSpot = parkingSpot.takeIf { it.isNotBlank() },
                                addAnother = true
                            )
                        },
                        enabled = !uiState.isLoading
                    ) {
                        Text("Salvar e adicionar outro")
                    }
                }

                Button(
                    onClick = {
                        viewModel.saveVehicle(
                            customerId = customerId,
                            vehicleId = vehicleId,
                            brand = brand,
                            model = model,
                            color = color,
                            plate = plate,
                            plateType = plateType,
                            category = category,
                            parkingSpot = parkingSpot.takeIf { it.isNotBlank() },
                            addAnother = false
                        )
                    },
                    enabled = !uiState.isLoading
                ) {
                    Text("Finalizar")
                }
            }
        }
    }
}

/** Formats the raw typing into the appropriate plate mask. */
private fun formatPlateInput(input: String, plateType: PlateType): String {
    return when (plateType) {
        PlateType.MERCOSUL -> {
            // LLLNLNN — positions: 0-2 letter, 3 digit, 4 letter, 5-6 digit
            val clean = input.filter { it.isLetterOrDigit() }.uppercase()
            buildString {
                for (i in clean.indices) {
                    if (length >= 7) break
                    val ch = clean[i]
                    when (length) {
                        0, 1, 2 -> if (ch.isLetter()) append(ch)
                        3 -> if (ch.isDigit()) append(ch)
                        4 -> if (ch.isLetter()) append(ch)
                        5, 6 -> if (ch.isDigit()) append(ch)
                    }
                }
            }
        }
        PlateType.OUTRA -> {
            // LLL-NNNN
            val letters = input.filter { it.isLetter() }.uppercase().take(3)
            val digits = input.filter { it.isDigit() }.take(4)
            if (digits.isEmpty()) letters else "$letters-$digits"
        }
    }
}

