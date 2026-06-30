package com.parkcontrol.features.monthlyCustomers.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.features.monthlyCustomers.domain.usecase.ActivateMonthlyCustomerUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetInactiveMonthlyCustomersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InactiveMonthlyCustomersViewModel(
    application: Application,
    private val getInactiveMonthlyCustomersUseCase: GetInactiveMonthlyCustomersUseCase,
    private val activateMonthlyCustomerUseCase: ActivateMonthlyCustomerUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MonthlyCustomersUiState())
    val uiState: StateFlow<MonthlyCustomersUiState> = _uiState

    init {
        loadCustomers()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            getInactiveMonthlyCustomersUseCase().collect { customers ->
                _uiState.value = _uiState.value.copy(customers = customers)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun activateCustomer(customerId: Int) {
        viewModelScope.launch {
            try {
                activateMonthlyCustomerUseCase(customerId)
                _uiState.value = _uiState.value.copy(successMessage = "Cliente ativado com sucesso")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Erro ao ativar cliente")
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}

