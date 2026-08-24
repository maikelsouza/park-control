package com.parkcontrol.features.monthlyCustomers.ui

import android.annotation.SuppressLint
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkcontrol.core.navigation.AppDrawerScaffold
import com.parkcontrol.core.navigation.AppRoutes
import com.parkcontrol.core.ui.masks.CurrencyMaskTransformation
import com.parkcontrol.core.ui.masks.PhoneMaskTransformation
import com.parkcontrol.core.ui.masks.onlyMoneyDigits
import com.parkcontrol.core.ui.masks.onlyPhoneDigits
import com.parkcontrol.core.ui.masks.toBrazilianPhoneMask
import com.parkcontrol.core.ui.theme.ParkControlTheme
import java.text.NumberFormat
import java.util.Locale

private val DueDayOptions = listOf("1", "5", "10", "15", "20", "25")
private val SexoOptions = listOf(
    "" to "Nao informar",
    "masculino" to "Masculino",
    "feminino" to "Feminino"
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveMonthlyCustomersScreen(
    onNavigate: (String) -> Unit,
    onNavigateForward: (String) -> Unit = onNavigate,
    currentRoute: String = AppRoutes.MonthlyCustomers.route,
    saveSuccessMessage: String? = null,
    onSaveSuccessMessageShown: () -> Unit = {}
) {
    AppDrawerScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { paddingValues ->
        val context = LocalContext.current
        val application = context.applicationContext as android.app.Application
        val factory = ActiveMonthlyCustomersViewModelFactory(application = application)
        val viewModel: ActiveMonthlyCustomersViewModel = viewModel(factory = factory)

        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        var customerIdToInactivate by remember { mutableStateOf<Int?>(null) }

        val filteredCustomers = uiState.customers.filter { customer ->
            val query = uiState.searchQuery.trim().lowercase(Locale.ROOT)
            if (query.isBlank()) true
            else {
                customer.name.contains(query, ignoreCase = true) ||
                    customer.vehicles.any { v -> v.plate.contains(query, ignoreCase = true) }
            }
        }

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.clearErrorMessage()
            }
        }

        LaunchedEffect(uiState.successMessage) {
            uiState.successMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.clearSuccessMessage()
            }
        }

        LaunchedEffect(saveSuccessMessage) {
            saveSuccessMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                onSaveSuccessMessageShown()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Clientes", style = MaterialTheme.typography.titleLarge)
                }

                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        label = { Text("Pesquisar por placa ou nome") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    if (filteredCustomers.isEmpty()) {
                        if (uiState.searchQuery.isBlank()) Text("Nenhum cliente cadastrado")
                        else Text("Nenhum cliente encontrado para essa pesquisa")
                    } else {
                        Text("Clientes ativos: ${filteredCustomers.size}")
                    }
                }

                items(filteredCustomers) { customer ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val primaryVehicle = customer.vehicles.firstOrNull()

                            Text(
                                text = customer.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (primaryVehicle != null) {
                                val extra = (customer.vehicles.size - 1).coerceAtLeast(0)
                                Text(
                                    text = buildString {
                                        append("${primaryVehicle.brand} ${primaryVehicle.model}".trim())
                                        if (primaryVehicle.plate.isNotBlank()) append(" • ${primaryVehicle.plate}")
                                        if (extra > 0) append(" (+$extra)")
                                    }
                                )
                            } else {
                                Text(
                                    text = "Nenhum veículo cadastrado",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (customer.isMonthly) {
                                Text("Mensalidade: ${customer.monthlyFeeCents.toCurrency()}")
                                Text(
                                    text = customer.dueDay?.let { "Vencimento: dia $it" }
                                        ?: "Vencimento: nao informado"
                                )
                            } else {
                                Text("Nao mensalista")
                            }

                            if (customer.phone.isNotEmpty()) {
                                Text("Telefone: ${customer.phone.toBrazilianPhoneMask()}")
                            }
                            if (customer.email.isNotEmpty()) {
                                Text("Email: ${customer.email}")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    onNavigateForward(AppRoutes.CustomerVehicles.createRoute(customer.id))
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.DirectionsCar,
                                        contentDescription = "Veículos"
                                    )
                                }
                                IconButton(onClick = {
                                    onNavigateForward(AppRoutes.MonthlyCustomerForm.createRoute(customer.id))
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = "Editar"
                                    )
                                }
                                TextButton(onClick = { customerIdToInactivate = customer.id }) {
                                    Text("Inativar")
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { onNavigateForward(AppRoutes.MonthlyCustomerForm.createRoute()) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Novo cliente")
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )

            if (customerIdToInactivate != null) {
                AlertDialog(
                    onDismissRequest = { customerIdToInactivate = null },
                    title = { Text("Inativar cliente") },
                    text = { Text("Esse cliente deixara de aparecer na lista de ativos.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.inactivateCustomer(customerIdToInactivate!!)
                                customerIdToInactivate = null
                            }
                        ) { Text("Inativar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { customerIdToInactivate = null }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyCustomerFormScreen(
    onNavigate: (String) -> Unit,
    customerId: Int?,
    onBack: () -> Unit,
    onSaveSuccess: (String) -> Unit,
    onNewCustomerSaved: (Int) -> Unit = {}
) {
    AppDrawerScaffold(
        currentRoute = AppRoutes.MonthlyCustomers.route,
        onNavigate = onNavigate
    ) { paddingValues ->
        val context = LocalContext.current
        val application = context.applicationContext as android.app.Application
        val factory = ActiveMonthlyCustomersViewModelFactory(application = application)
        val viewModel: ActiveMonthlyCustomersViewModel = viewModel(factory = factory)

        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        var name by rememberSaveable(customerId) { mutableStateOf("") }
        var phone by rememberSaveable(customerId) { mutableStateOf("") }
        var email by rememberSaveable(customerId) { mutableStateOf("") }
        var sexo by rememberSaveable(customerId) { mutableStateOf("") }
        var emailFieldTouched by rememberSaveable(customerId) { mutableStateOf(false) }
        var isMonthly by rememberSaveable(customerId) { mutableStateOf(true) }
        var monthlyFee by rememberSaveable(customerId) { mutableStateOf("") }
        var dueDay by rememberSaveable(customerId) { mutableStateOf("") }
        var didPrefill by rememberSaveable(customerId) { mutableStateOf(false) }
        var showInactivateDialog by remember { mutableStateOf(false) }
        var sexoMenuExpanded by remember { mutableStateOf(false) }
        var dueDayMenuExpanded by remember { mutableStateOf(false) }

        LaunchedEffect(customerId) {
            viewModel.loadCustomerForEdit(customerId)
        }

        LaunchedEffect(uiState.selectedCustomer?.id, customerId) {
            val customer = uiState.selectedCustomer
            if (customerId != null && customer != null && !didPrefill) {
                name = customer.name
                phone = customer.phone.onlyPhoneDigits().take(11)
                email = customer.email
                sexo = customer.sexo.orEmpty()
                isMonthly = customer.isMonthly
                monthlyFee = customer.monthlyFeeCents.toMoneyDigitsInput()
                dueDay = customer.dueDay?.toString().orEmpty()
                didPrefill = true
            }
        }

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.clearErrorMessage()
            }
        }

        LaunchedEffect(uiState.successMessage) {
            uiState.successMessage?.let { message ->
                viewModel.clearSuccessMessage()
                if (message.isNotBlank() && uiState.savedCustomerId == null) {
                    onSaveSuccess(message)
                }
            }
        }

        LaunchedEffect(uiState.savedCustomerId) {
            uiState.savedCustomerId?.let { newId ->
                viewModel.clearSavedCustomerId()
                onNewCustomerSaved(newId)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Voltar"
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (customerId == null) "Novo Cliente" else "Editar Cliente",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { typed -> phone = typed.onlyPhoneDigits().take(11) },
                        label = { Text("Telefone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PhoneMaskTransformation
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { typed ->
                            emailFieldTouched = true
                            email = typed.sanitizeEmailInput()
                        },
                        label = { Text("Email") },
                        placeholder = { Text("ex: nome@dominio.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = emailFieldTouched && email.isNotBlank() && !email.looksLikeEmail(),
                        supportingText = {
                            if (emailFieldTouched && email.isNotBlank() && !email.looksLikeEmail()) {
                                Text("Informe um e-mail válido")
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            capitalization = KeyboardCapitalization.None,
                            autoCorrectEnabled = false
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = sexoMenuExpanded,
                        onExpandedChange = { sexoMenuExpanded = !sexoMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = sexo.toSexoLabel(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sexo (opcional)") },
                            placeholder = { Text("Selecione") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexoMenuExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor(
                                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                    enabled = true
                                )
                                .fillMaxWidth(),
                            singleLine = true
                        )

                        ExposedDropdownMenu(
                            expanded = sexoMenuExpanded,
                            onDismissRequest = { sexoMenuExpanded = false }
                        ) {
                            SexoOptions.forEach { (value, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        sexo = value
                                        sexoMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isMonthly,
                            onCheckedChange = { checked ->
                                isMonthly = checked
                                if (!checked) {
                                    monthlyFee = ""
                                    dueDay = ""
                                }
                            }
                        )
                        Text("É mensalista?")
                    }

                    if (isMonthly) {
                        OutlinedTextField(
                            value = monthlyFee,
                            onValueChange = { typed -> monthlyFee = typed.onlyMoneyDigits().take(11) },
                            label = { Text("Mensalidade fixa *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = CurrencyMaskTransformation
                        )

                        ExposedDropdownMenuBox(
                            expanded = dueDayMenuExpanded,
                            onExpandedChange = { dueDayMenuExpanded = !dueDayMenuExpanded }
                        ) {
                            OutlinedTextField(
                                value = dueDay,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Dia de vencimento *") },
                                placeholder = { Text("Selecione") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dueDayMenuExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor(
                                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                        enabled = true
                                    )
                                    .fillMaxWidth(),
                                singleLine = true
                            )

                            ExposedDropdownMenu(
                                expanded = dueDayMenuExpanded,
                                onDismissRequest = { dueDayMenuExpanded = false }
                            ) {
                                DueDayOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            dueDay = option
                                            dueDayMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (customerId != null) {
                        OutlinedButton(
                            onClick = {
                                onNavigate(AppRoutes.CustomerVehicles.createRoute(customerId))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DirectionsCar,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Gerenciar Veículos")
                        }
                    }
                }

                if (customerId != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showInactivateDialog = true }) {
                            Text("Inativar")
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.saveCustomer(
                            customerId = customerId,
                            name = name,
                            phone = phone.onlyPhoneDigits().take(11),
                            email = email,
                            sexo = sexo,
                            isMonthly = isMonthly,
                            monthlyFee = monthlyFee,
                            dueDay = dueDay
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (customerId == null) "Salvar" else "Atualizar",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )

            if (showInactivateDialog && customerId != null) {
                AlertDialog(
                    onDismissRequest = { showInactivateDialog = false },
                    title = { Text("Inativar cliente") },
                    text = { Text("Deseja inativar este cliente?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.inactivateCustomer(customerId)
                                showInactivateDialog = false
                                onBack()
                            }
                        ) { Text("Inativar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showInactivateDialog = false }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}

private fun Int?.toCurrency(): String {
    if (this == null) return "Nao informado"
    val ptBrLocale = Locale.Builder().setLanguage("pt").setRegion("BR").build()
    val formatter = NumberFormat.getCurrencyInstance(ptBrLocale)
    return formatter.format(this / 100.0)
}

private fun Int?.toMoneyDigitsInput(): String {
    if (this == null) return ""
    return this.toString()
}


private fun String.sanitizeEmailInput(): String {
    val allowedChars = buildString(length) {
        for (char in this@sanitizeEmailInput.lowercase(Locale.ROOT)) {
            if (char.isLetterOrDigit() || char in setOf('@', '.', '_', '-', '+')) append(char)
        }
    }
    val parts = allowedChars.split('@', limit = 2)
    return when {
        parts.size == 1 -> parts[0].trim()
        else -> "${parts[0].trim()}@${parts[1].trim()}"
    }.take(254)
}

private fun String.looksLikeEmail(): Boolean {
    if (isBlank()) return false
    return Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(trim())
}

private fun String.toSexoLabel(): String {
    return when (trim().lowercase(Locale.ROOT)) {
        "masculino" -> "Masculino"
        "feminino" -> "Feminino"
        else -> ""
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActiveMonthlyCustomersPreview() {
    ParkControlTheme {
        ActiveMonthlyCustomersScreen(onNavigate = {})
    }
}


