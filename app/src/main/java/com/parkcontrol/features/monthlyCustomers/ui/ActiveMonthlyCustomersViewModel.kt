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
        isMonthly: Boolean,
        monthlyFee: String,
        dueDay: String,
        plates: List<String>
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

        val normalizedName = name.trim()
        val normalizedPhone = phone.filter(Char::isDigit).take(11)

        val existingPlates = _uiState.value.customers
            .asSequence()
            .filter { customer -> customer.id != customerId }
            .flatMap { customer -> customer.plates.asSequence() }
            .map { plate -> plate.trim().uppercase() }
            .toSet()
        val duplicatePlate = normalizedPlates.firstOrNull { plate -> plate in existingPlates }
        if (duplicatePlate != null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Placa ja cadastrada: $duplicatePlate"
            )
            return
        }

        if (normalizedPhone.isNotBlank()) {
            val phoneAlreadyUsed = _uiState.value.customers
                .any { customer ->
                    customer.id != customerId &&
                        customer.phone.filter(Char::isDigit).take(11) == normalizedPhone
                }
            if (phoneAlreadyUsed) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Telefone ja cadastrado para outro cliente"
                )
                return
            }
        }

        val monthlyFeeCents: Int?
        val dueDayValue: Int?

        if (isMonthly) {
            monthlyFeeCents = if (monthlyFee.isBlank()) {
                null
            } else {
                parseFeeToCents(monthlyFee) ?: run {
                    _uiState.value = _uiState.value.copy(errorMessage = "Valor mensal invalido")
                    return
                }
            }

            dueDayValue = if (dueDay.isBlank()) {
                null
            } else {
                dueDay.toIntOrNull()?.takeIf { it in 1..31 } ?: run {
                    _uiState.value = _uiState.value.copy(errorMessage = "Vencimento deve ser entre 1 e 31")
                    return
                }
            }
        } else {
            monthlyFeeCents = null
            dueDayValue = null
        }

        viewModelScope.launch {
            try {
                val existing = customerId?.let { getMonthlyCustomerByIdUseCase(it) }
                val now = System.currentTimeMillis()
                val customer = MonthlyCustomer(
                    id = existing?.id ?: 0,
                    name = normalizedName,
                    phone = normalizedPhone,
                    isMonthly = isMonthly,
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

                _uiState.value = _uiState.value.copy(successMessage = if (customerId == null) "Cliente salvo com sucesso" else "Cliente atualizado com sucesso")
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
        val digitsOnly = input.filter(Char::isDigit)
        if (digitsOnly.isNotEmpty() && digitsOnly.length == input.trim().length) {
            return digitsOnly.toLongOrNull()?.takeIf { it <= Int.MAX_VALUE }?.toInt()
        }

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


