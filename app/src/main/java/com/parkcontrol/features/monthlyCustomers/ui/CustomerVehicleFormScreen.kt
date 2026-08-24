package com.parkcontrol.features.monthlyCustomers.ui

import androidx.compose.foundation.layout.Arrangement
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
import com.parkcontrol.core.ui.masks.formatPlateInput
import com.parkcontrol.core.ui.masks.plateInputPlaceholder
import com.parkcontrol.features.monthlyCustomers.domain.model.PlateType
import com.parkcontrol.features.monthlyCustomers.domain.model.VehicleCategory
import java.util.Locale

private val CommonBrands = listOf(
    "Aprilia", "Audi", "Bajaj", "Benelli", "BMW", "BYD", "Caoa Chery", "Chevrolet",
    "Citroën", "Dafra", "Ducati", "Fiat", "Ford", "GasGas", "Harley-Davidson",
    "Haojue", "Honda", "Husqvarna", "Hyundai", "Indian", "Jeep", "Kawasaki", "Kia",
    "KTM", "Land Rover", "Mercedes-Benz", "Mitsubishi", "Moto Guzzi", "Nissan",
    "Peugeot", "Renault", "Royal Enfield", "Shineray", "Suzuki", "Toyota", "Triumph",
    "Volkswagen", "Volvo", "Yamaha", "Outro"
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
    var customBrand by rememberSaveable(vehicleId) { mutableStateOf("") }
    var model by rememberSaveable(vehicleId) { mutableStateOf("") }
    var color by rememberSaveable(vehicleId) { mutableStateOf("") }
    var plate by rememberSaveable(vehicleId) { mutableStateOf("") }
    var plateType by rememberSaveable(vehicleId) { mutableStateOf(PlateType.MERCOSUL) }
    var category by rememberSaveable(vehicleId) { mutableStateOf<VehicleCategory?>(null) }
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
            brand = if (v.brand in CommonBrands) v.brand else "Outro"
            customBrand = if (v.brand !in CommonBrands) v.brand else ""
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
                customBrand = ""
                model = ""
                color = ""
                plate = ""
                plateType = PlateType.MERCOSUL
                category = null
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
                // ── Categoria ──────────────────────────────────────────────
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category?.displayName.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        placeholder = { Text("Selecione") },
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

                // ── Marca Customizada (quando "Outro" é selecionado) ──────
                if (brand == "Outro") {
                    val customBrandError = getCustomBrandValidationError(customBrand)
                    OutlinedTextField(
                        value = customBrand,
                        onValueChange = { customBrand = it },
                        label = { Text("Marca *") },
                        placeholder = { Text("Digite a marca") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        isError = customBrandError != null,
                        supportingText = {
                            if (customBrandError != null) {
                                Text(customBrandError, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
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
                    placeholder = { Text(plateInputPlaceholder(plateType)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false
                    ),
                    supportingText = { Text(plateType.displayName) }
                )


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
                val finalBrand = if (brand == "Outro") customBrand else brand
                val customBrandError = if (brand == "Outro") getCustomBrandValidationError(customBrand) else null
                val isSaveEnabled = !uiState.isLoading && finalBrand.isNotBlank() && customBrandError == null

                if (vehicleId == null) {
                    OutlinedButton(
                        onClick = {
                            viewModel.saveVehicle(
                                customerId = customerId,
                                vehicleId = null,
                                brand = finalBrand,
                                model = model,
                                color = color,
                                plate = plate,
                                plateType = plateType,
                                category = category ?: VehicleCategory.OUTRO,
                                parkingSpot = parkingSpot.takeIf { it.isNotBlank() },
                                addAnother = true
                            )
                        },
                        enabled = isSaveEnabled
                    ) {
                        Text("Salvar e adicionar outro")
                    }
                }

                Button(
                    onClick = {
                        viewModel.saveVehicle(
                            customerId = customerId,
                            vehicleId = vehicleId,
                            brand = finalBrand,
                            model = model,
                            color = color,
                            plate = plate,
                            plateType = plateType,
                            category = category ?: VehicleCategory.OUTRO,
                            parkingSpot = parkingSpot.takeIf { it.isNotBlank() },
                            addAnother = false
                        )
                    },
                    enabled = isSaveEnabled
                ) {
                    Text("Finalizar")
                }
            }
        }
    }
}

/** Valida se a marca customizada é similar a alguma marca já listada. */
private fun getCustomBrandValidationError(customBrand: String): String? {
    if (customBrand.isBlank()) {
        return "Campo obrigatório"
    }

    val normalizedCustom = customBrand.trim().lowercase()
    val brandsWithoutOutro = CommonBrands.filter { it != "Outro" }

    for (listedBrand in brandsWithoutOutro) {
        val normalizedListed = listedBrand.lowercase()
        val similarity = calculateSimilarity(normalizedCustom, normalizedListed)

        // Se a similaridade for >= 70%, considera como duplicada
        if (similarity >= 0.7) {
            return "Marca similar a \"$listedBrand\" já está listada"
        }
    }

    return null
}

/** Calcula a similaridade entre duas strings (0 a 1, onde 1 é idêntica). */
private fun calculateSimilarity(str1: String, str2: String): Double {
    val maxLength = maxOf(str1.length, str2.length)
    if (maxLength == 0) return 1.0

    val distance = levenshteinDistance(str1, str2)
    return 1.0 - (distance.toDouble() / maxLength)
}

/** Calcula a distância de Levenshtein entre duas strings. */
private fun levenshteinDistance(str1: String, str2: String): Int {
    val matrix = Array(str1.length + 1) { IntArray(str2.length + 1) }

    for (i in 0..str1.length) {
        matrix[i][0] = i
    }
    for (j in 0..str2.length) {
        matrix[0][j] = j
    }

    for (i in 1..str1.length) {
        for (j in 1..str2.length) {
            val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
            matrix[i][j] = minOf(
                matrix[i - 1][j] + 1,      // deletion
                matrix[i][j - 1] + 1,      // insertion
                matrix[i - 1][j - 1] + cost // substitution
            )
        }
    }

    return matrix[str1.length][str2.length]
}

