package com.parkcontrol.features.monthlyCustomers.ui

import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle

data class CustomerVehicleUiState(
    val customerName: String = "",
    val vehicles: List<CustomerVehicle> = emptyList(),
    val selectedVehicle: CustomerVehicle? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val saveAndAddAnother: Boolean = false  // true when "Salvar e adicionar outro" is triggered
)

