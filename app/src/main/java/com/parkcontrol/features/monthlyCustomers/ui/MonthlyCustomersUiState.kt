package com.parkcontrol.features.monthlyCustomers.ui

import com.parkcontrol.features.monthlyCustomers.domain.model.MonthlyCustomer

data class MonthlyCustomersUiState(
    val customers: List<MonthlyCustomer> = emptyList(),
    val searchQuery: String = "",
    val selectedCustomer: MonthlyCustomer? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

