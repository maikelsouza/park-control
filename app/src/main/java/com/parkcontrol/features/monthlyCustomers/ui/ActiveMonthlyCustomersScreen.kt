package com.parkcontrol.features.monthlyCustomers.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkcontrol.core.navigation.AppDrawerScaffold
import com.parkcontrol.core.navigation.AppRoutes
import com.parkcontrol.core.ui.theme.ParkControlTheme
import java.text.NumberFormat
import java.util.Locale

private val PhoneMaskTransformation = BrazilianPhoneVisualTransformation()

private class BrazilianPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.onlyPhoneDigits().take(11)

        val masked = digits.toBrazilianPhoneMask()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, digits.length)
                val transformedOffset = when {
                    safeOffset == 0 -> 0
                    safeOffset <= 2 -> safeOffset + 1
                    digits.length <= 10 && safeOffset <= 6 -> safeOffset + 3
                    else -> safeOffset + 4
                }
                return transformedOffset.coerceAtMost(masked.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, masked.length)
                return masked.take(safeOffset).count(Char::isDigit).coerceAtMost(digits.length)
            }
        }

        return TransformedText(AnnotatedString(masked), offsetMapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveMonthlyCustomersScreen(
    onNavigate: (String) -> Unit,
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
            if (query.isBlank()) {
                true
            } else {
                customer.name.contains(query, ignoreCase = true) ||
                    customer.plates.any { plate -> plate.contains(query, ignoreCase = true) }
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
                        if (uiState.searchQuery.isBlank()) {
                            Text("Nenhum cliente cadastrado")
                        } else {
                            Text("Nenhum cliente encontrado para essa pesquisa")
                        }
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
                            val primaryPlate = customer.plates.firstOrNull().orEmpty()
                            val extraPlates = (customer.plates.size - 1).coerceAtLeast(0)

                            Text(
                                text = customer.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (extraPlates > 0) {
                                    "Placa principal: $primaryPlate (+$extraPlates)"
                                } else {
                                    "Placa principal: $primaryPlate"
                                },
                            )
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        onNavigate(AppRoutes.MonthlyCustomerForm.createRoute(customer.id))
                                    }
                                ) {
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
                onClick = { onNavigate(AppRoutes.MonthlyCustomerForm.createRoute()) },
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
                        ) {
                            Text("Inativar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { customerIdToInactivate = null }) {
                            Text("Cancelar")
                        }
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
    onSaveSuccess: (String) -> Unit
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
        var isMonthly by rememberSaveable(customerId) { mutableStateOf(true) }
        var monthlyFee by rememberSaveable(customerId) { mutableStateOf("") }
        var dueDay by rememberSaveable(customerId) { mutableStateOf("") }
        val plates = remember(customerId) { mutableStateListOf("") }
        var didPrefill by rememberSaveable(customerId) { mutableStateOf(false) }
        var showInactivateDialog by remember { mutableStateOf(false) }

        LaunchedEffect(customerId) {
            viewModel.loadCustomerForEdit(customerId)
        }

        LaunchedEffect(uiState.selectedCustomer?.id, customerId) {
            val customer = uiState.selectedCustomer
            if (customerId != null && customer != null && !didPrefill) {
                name = customer.name
                phone = customer.phone.onlyPhoneDigits().take(11)
                isMonthly = customer.isMonthly
                monthlyFee = customer.monthlyFeeCents.toMoneyInput()
                dueDay = customer.dueDay?.toString().orEmpty()
                plates.clear()
                plates.addAll(customer.plates.ifEmpty { listOf("") })
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
                if (message.isNotBlank()) {
                    onSaveSuccess(message)
                }
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
                            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar")
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
                            onValueChange = { monthlyFee = it },
                            label = { Text("Mensalidade fixa (ex: 250,00) *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = dueDay,
                            onValueChange = { dueDay = it },
                            label = { Text("Dia de vencimento (1-31) *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Text("Placas")
                    plates.forEachIndexed { index, plate ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = plate,
                                onValueChange = { plates[index] = it },
                                label = {
                                    Text(
                                        if (index == 0) {
                                            "Placa principal *"
                                        } else {
                                            "Placa ${index + 1}"
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            if (plates.size > 1) {
                                TextButton(onClick = { plates.removeAt(index) }) {
                                    Text("Remover")
                                }
                            }
                        }
                    }

                    OutlinedButton(onClick = { plates.add("") }) {
                        Text("Adicionar placa")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (customerId != null) {
                        TextButton(onClick = { showInactivateDialog = true }) {
                            Text("Inativar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Button(
                        onClick = {
                            viewModel.saveCustomer(
                                customerId = customerId,
                                name = name,
                                phone = phone.onlyPhoneDigits().take(11),
                                isMonthly = isMonthly,
                                monthlyFee = monthlyFee,
                                dueDay = dueDay,
                                plates = plates.toList()
                            )
                        },
                        enabled = !uiState.isLoading
                    ) {
                        Text("Salvar")
                    }
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
                        ) {
                            Text("Inativar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showInactivateDialog = false }) {
                            Text("Cancelar")
                        }
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

private fun Int?.toMoneyInput(): String {
    if (this == null) return ""
    return String.format(Locale.US, "%.2f", this / 100.0).replace('.', ',')
}

private fun String.onlyPhoneDigits(): String = filter(Char::isDigit)

private fun String.toBrazilianPhoneMask(): String {
    val digits = onlyPhoneDigits().take(11)
    return when {
        digits.isEmpty() -> ""
        digits.length <= 2 -> "(${digits}"
        digits.length <= 6 -> "(${digits.substring(0, 2)}) ${digits.substring(2)}"
        digits.length <= 10 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 6)}-${digits.substring(6)}"
        else -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}"
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


