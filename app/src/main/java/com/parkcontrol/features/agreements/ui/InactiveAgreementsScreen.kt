package com.parkcontrol.features.agreements.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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

@Composable
fun InactiveAgreementsScreen(
    onNavigate: (String) -> Unit,
    currentRoute: String = AppRoutes.AgreementsInactive.route
) {
    AppDrawerScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { paddingValues ->
        val context = LocalContext.current
        val application = context.applicationContext as android.app.Application
        val factory = InactiveAgreementsViewModelFactory(application = application)
        val viewModel: InactiveAgreementsViewModel = viewModel(factory = factory)

        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        var agreementIdToActivate by remember { mutableStateOf<Int?>(null) }

        val filteredAgreements = uiState.agreements.filter { agreement ->
            val query = uiState.searchQuery.trim().lowercase(Locale.ROOT)
            if (query.isBlank()) {
                true
            } else {
                agreement.name.contains(query, ignoreCase = true) ||
                    agreement.contactName.contains(query, ignoreCase = true) ||
                    agreement.phone.contains(query)
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
                    Text("Convênios inativos", style = MaterialTheme.typography.titleLarge)
                }

                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        label = { Text("Pesquisar por nome ou contato") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    if (filteredAgreements.isEmpty()) {
                        if (uiState.searchQuery.isBlank()) {
                            Text("Nenhum convênio inativo")
                        } else {
                            Text("Nenhum convênio encontrado para essa pesquisa")
                        }
                    } else {
                        Text("Convênios inativos: ${filteredAgreements.size}")
                    }
                }

                items(filteredAgreements) { agreement ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = agreement.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Responsável: ${agreement.contactName}")

                            if (agreement.phone.isNotBlank()) {
                                Text("Telefone: ${agreement.phone}")
                            }

                            Text("Desconto: ${agreement.discountCents.toCurrency()}")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { agreementIdToActivate = agreement.id }) {
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

            if (agreementIdToActivate != null) {
                AlertDialog(
                    onDismissRequest = { agreementIdToActivate = null },
                    title = { Text("Ativar convênio") },
                    text = { Text("Esse convênio voltará para a lista de ativos.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.activateAgreement(agreementIdToActivate!!)
                                agreementIdToActivate = null
                            }
                        ) {
                            Text("Ativar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { agreementIdToActivate = null }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

private fun Int.toCurrency(): String {
    val ptBrLocale = Locale.Builder().setLanguage("pt").setRegion("BR").build()
    val formatter = NumberFormat.getCurrencyInstance(ptBrLocale)
    return formatter.format(this / 100.0)
}

