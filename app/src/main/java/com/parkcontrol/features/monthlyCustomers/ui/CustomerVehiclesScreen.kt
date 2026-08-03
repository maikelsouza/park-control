package com.parkcontrol.features.monthlyCustomers.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.parkcontrol.core.navigation.AppRoutes
import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerVehiclesScreen(
    customerId: Int,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application
    val factory = CustomerVehicleViewModelFactory(application)
    val viewModel: CustomerVehicleViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var vehicleIdToDelete by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(customerId) {
        viewModel.loadForCustomer(customerId)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Veículos")
                        if (uiState.customerName.isNotBlank()) {
                            Text(
                                text = uiState.customerName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(AppRoutes.CustomerVehicleForm.createRoute(customerId)) }
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Adicionar veículo")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.vehicles.isEmpty()) {
                Text(
                    text = "Nenhum veículo cadastrado.\nToque em + para adicionar.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.vehicles) { vehicle ->
                        VehicleCard(
                            vehicle = vehicle,
                            onEdit = {
                                onNavigate(AppRoutes.CustomerVehicleForm.createRoute(customerId, vehicle.id))
                            },
                            onDelete = { vehicleIdToDelete = vehicle.id }
                        )
                    }
                }
            }
        }
    }

    if (vehicleIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { vehicleIdToDelete = null },
            title = { Text("Remover veículo") },
            text = { Text("Deseja remover este veículo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteVehicle(vehicleIdToDelete!!)
                        vehicleIdToDelete = null
                    }
                ) { Text("Remover") }
            },
            dismissButton = {
                TextButton(onClick = { vehicleIdToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun VehicleCard(
    vehicle: CustomerVehicle,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${vehicle.brand} ${vehicle.model}".trim().ifBlank { "Veículo sem nome" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (vehicle.plate.isNotBlank()) {
                        Text("Placa: ${vehicle.plate}  (${vehicle.plateType.displayName})")
                    }
                    if (vehicle.color.isNotBlank()) Text("Cor: ${vehicle.color}")
                    Text("Categoria: ${vehicle.category.displayName}")
                    if (!vehicle.parkingSpot.isNullOrBlank()) {
                        Text("Vaga: ${vehicle.parkingSpot}")
                    }
                    if (vehicle.isPrimary) {
                        Text(
                            text = "Principal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = "Remover",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

