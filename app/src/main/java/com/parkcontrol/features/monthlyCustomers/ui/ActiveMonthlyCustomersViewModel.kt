package com.parkcontrol.features.monthlyCustomers.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.features.monthlyCustomers.domain.model.MonthlyCustomer
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetMonthlyCustomerByIdUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetActiveMonthlyCustomersUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.InactivateMonthlyCustomerUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.SaveMonthlyCustomerUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.UpdateMonthlyCustomerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class ActiveMonthlyCustomersViewModel(
    application: Application,
    private val getActiveMonthlyCustomersUseCase: GetActiveMonthlyCustomersUseCase,
    private val saveMonthlyCustomerUseCase: SaveMonthlyCustomerUseCase,
    private val getMonthlyCustomerByIdUseCase: GetMonthlyCustomerByIdUseCase,
    private val updateMonthlyCustomerUseCase: UpdateMonthlyCustomerUseCase,
    private val inactivateMonthlyCustomerUseCase: InactivateMonthlyCustomerUseCase
) : AndroidViewModel(application) {

    // UI State
    private val _uiState = MutableStateFlow(MonthlyCustomersUiState())
    val uiState: StateFlow<MonthlyCustomersUiState> = _uiState

    init {
        loadCustomers()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            getActiveMonthlyCustomersUseCase().collect { customers ->
                _uiState.value = _uiState.value.copy(customers = customers)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun loadCustomerForEdit(customerId: Int?) {
        if (customerId == null) {
            _uiState.value = _uiState.value.copy(selectedCustomer = null)
            return
        }

        viewModelScope.launch {
            val customer = getMonthlyCustomerByIdUseCase(customerId)
            _uiState.value = if (customer == null) {
                _uiState.value.copy(
                    selectedCustomer = null,
                    errorMessage = "Cliente nao encontrado"
                )
            } else {
                _uiState.value.copy(selectedCustomer = customer)
            }
        }
    }

    fun saveCustomer(
        customerId: Int?,
        name: String,
        phone: String,
        monthlyFee: String,
        dueDay: String,
        plates: List<String>,
        onSuccess: () -> Unit
    ) {
        val normalizedPlates = plates
            .map { it.trim().uppercase() }
            .filter { it.isNotBlank() }
            .distinct()

        if (name.isBlank() || normalizedPlates.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Nome e pelo menos uma placa sao obrigatorios"
            )
            return
        }

        val monthlyFeeCents = parseFeeToCents(monthlyFee)
        if (monthlyFeeCents == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Valor mensal invalido")
            return
        }

        val dueDayValue = dueDay.toIntOrNull()
        if (dueDayValue == null || dueDayValue !in 1..31) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vencimento deve ser entre 1 e 31")
            return
        }

        viewModelScope.launch {
            try {
                val existing = customerId?.let { getMonthlyCustomerByIdUseCase(it) }
                val now = System.currentTimeMillis()
                val customer = MonthlyCustomer(
                    id = existing?.id ?: 0,
                    name = name.trim(),
                    phone = phone.trim(),
                    monthlyFeeCents = monthlyFeeCents,
                    dueDay = dueDayValue,
                    plates = normalizedPlates,
                    isActive = true,
                    createdAt = existing?.createdAt ?: now,
                    updatedAt = now
                )

                if (customerId == null) {
                    saveMonthlyCustomerUseCase(customer)
                } else {
                    updateMonthlyCustomerUseCase(customer)
                }

                _uiState.value = _uiState.value.copy(successMessage = "Cliente salvo com sucesso")
                onSuccess()
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Erro ao salvar cliente")
            }
        }
    }

    fun inactivateCustomer(customerId: Int) {
        viewModelScope.launch {
            try {
                inactivateMonthlyCustomerUseCase(customerId)
                _uiState.value = _uiState.value.copy(successMessage = "Cliente inativado com sucesso")
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Erro ao inativar cliente")
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    private fun parseFeeToCents(input: String): Int? {
        val normalized = input
            .replace("R$", "")
            .trim()
            .replace(',', '.')

        val value = normalized.toBigDecimalOrNull() ?: return null
        if (value < BigDecimal.ZERO) return null

        return value
            .multiply(BigDecimal(100))
            .setScale(0, RoundingMode.HALF_UP)
            .toInt()
    }
}


