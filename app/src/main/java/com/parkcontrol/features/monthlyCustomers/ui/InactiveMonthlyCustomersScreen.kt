package com.parkcontrol.features.monthlyCustomers.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkcontrol.core.navigation.AppDrawerScaffold
import com.parkcontrol.core.navigation.AppRoutes
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField

@Composable
fun InactiveMonthlyCustomersScreen(
    onNavigate: (String) -> Unit,
    currentRoute: String = AppRoutes.MonthlyCustomersInactive.route
) {
    AppDrawerScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { paddingValues ->
        val context = LocalContext.current
        val application = context.applicationContext as android.app.Application
        val factory = InactiveMonthlyCustomersViewModelFactory(application = application)
        val viewModel: InactiveMonthlyCustomersViewModel = viewModel(factory = factory)

        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        var customerIdToActivate by remember { mutableStateOf<Int?>(null) }

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
                    Text("Clientes Inativos", style = MaterialTheme.typography.titleLarge)
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
                            Text("Nenhum cliente inativo")
                        } else {
                            Text("Nenhum cliente encontrado para essa pesquisa")
                        }
                    } else {
                        Text("Clientes inativos: ${filteredCustomers.size}")
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
                                }
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
                                Text("Telefone: ${customer.phone}")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { customerIdToActivate = customer.id }) {
                                    Text("Ativar")
                                }
                            }
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )

            if (customerIdToActivate != null) {
                AlertDialog(
                    onDismissRequest = { customerIdToActivate = null },
                    title = { Text("Ativar cliente") },
                    text = { Text("Esse cliente voltara para a lista de ativos.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.activateCustomer(customerIdToActivate!!)
                                customerIdToActivate = null
                            }
                        ) {
                            Text("Ativar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { customerIdToActivate = null }) {
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

