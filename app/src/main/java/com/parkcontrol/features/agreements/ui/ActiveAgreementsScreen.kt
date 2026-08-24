package com.parkcontrol.features.agreements.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun ActiveAgreementsScreen(
    onNavigate: (String) -> Unit,
    onNavigateForward: (String) -> Unit = onNavigate,
    currentRoute: String = AppRoutes.AgreementsActive.route
) {
    AppDrawerScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { paddingValues ->
        val context = LocalContext.current
        val application = context.applicationContext as android.app.Application
        val factory = ActiveAgreementsViewModelFactory(application = application)
        val viewModel: ActiveAgreementsViewModel = viewModel(factory = factory)

        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        var agreementIdToInactivate by remember { mutableStateOf<Int?>(null) }

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
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Convênios ativos", style = MaterialTheme.typography.titleLarge)
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
                            Text("Nenhum convênio cadastrado")
                        } else {
                            Text("Nenhum convênio encontrado para essa pesquisa")
                        }
                    } else {
                        Text("Convênios ativos: ${filteredAgreements.size}")
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

                            if (agreement.email.isNotBlank()) {
                                Text("Email: ${agreement.email}")
                            }

                            Text("Desconto: ${agreement.discountCents.toCurrency()}")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    onNavigateForward(AppRoutes.AgreementForm.createRoute(agreement.id))
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = "Editar"
                                    )
                                }
                                TextButton(onClick = { agreementIdToInactivate = agreement.id }) {
                                    Text("Inativar")
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { onNavigateForward(AppRoutes.AgreementForm.createRoute()) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Novo convênio")
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )

            if (agreementIdToInactivate != null) {
                AlertDialog(
                    onDismissRequest = { agreementIdToInactivate = null },
                    title = { Text("Inativar convênio") },
                    text = { Text("Esse convênio deixará de aparecer na lista de ativos.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.inactivateAgreement(agreementIdToInactivate!!)
                                agreementIdToInactivate = null
                            }
                        ) {
                            Text("Inativar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { agreementIdToInactivate = null }) {
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

