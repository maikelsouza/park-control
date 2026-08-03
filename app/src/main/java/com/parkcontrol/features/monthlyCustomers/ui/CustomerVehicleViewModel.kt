package com.parkcontrol.features.monthlyCustomers.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle
import com.parkcontrol.features.monthlyCustomers.domain.model.PlateType
import com.parkcontrol.features.monthlyCustomers.domain.model.VehicleCategory
import com.parkcontrol.features.monthlyCustomers.domain.usecase.DeleteVehicleUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetMonthlyCustomerByIdUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetVehicleByIdUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetVehiclesByCustomerUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.SaveVehicleUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.UpdateVehicleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomerVehicleViewModel(
    application: Application,
    private val getVehiclesByCustomerUseCase: GetVehiclesByCustomerUseCase,
    private val getVehicleByIdUseCase: GetVehicleByIdUseCase,
    private val saveVehicleUseCase: SaveVehicleUseCase,
    private val updateVehicleUseCase: UpdateVehicleUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    private val getMonthlyCustomerByIdUseCase: GetMonthlyCustomerByIdUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CustomerVehicleUiState())
    val uiState: StateFlow<CustomerVehicleUiState> = _uiState

    fun loadForCustomer(customerId: Int) {
        viewModelScope.launch {
            val customer = getMonthlyCustomerByIdUseCase(customerId)
            _uiState.value = _uiState.value.copy(customerName = customer?.name.orEmpty())
        }
        viewModelScope.launch {
            getVehiclesByCustomerUseCase(customerId).collect { vehicles ->
                _uiState.value = _uiState.value.copy(vehicles = vehicles)
            }
        }
    }

    fun loadVehicleForEdit(vehicleId: Int?) {
        if (vehicleId == null) {
            _uiState.value = _uiState.value.copy(selectedVehicle = null)
            return
        }
        viewModelScope.launch {
            val vehicle = getVehicleByIdUseCase(vehicleId)
            _uiState.value = _uiState.value.copy(selectedVehicle = vehicle)
        }
    }

    /**
     * @param addAnother when true, signals the UI to reset the form for a new vehicle.
     */
    fun saveVehicle(
        customerId: Int,
        vehicleId: Int?,
        brand: String,
        model: String,
        color: String,
        plate: String,
        plateType: PlateType,
        category: VehicleCategory,
        parkingSpot: String?,
        addAnother: Boolean = false
    ) {
        val normalizedPlate = plate.trim().uppercase()
        if (normalizedPlate.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Placa é obrigatória")
            return
        }

        val plateError = validatePlate(normalizedPlate, plateType)
        if (plateError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = plateError)
            return
        }

        if (brand.isBlank() && model.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Informe ao menos marca ou modelo")
            return
        }

        viewModelScope.launch {
            try {
                // Check plate uniqueness
                val existing = _uiState.value.vehicles
                val conflicting = existing.any { v ->
                    v.id != (vehicleId ?: 0) &&
                        v.plate.trim().uppercase() == normalizedPlate
                }
                if (conflicting) {
                    _uiState.value = _uiState.value.copy(errorMessage = "Placa $normalizedPlate já cadastrada")
                    return@launch
                }

                val existingVehicle = vehicleId?.let { getVehicleByIdUseCase(it) }
                val now = System.currentTimeMillis()
                val isFirstVehicle = existingVehicle == null && _uiState.value.vehicles.isEmpty()

                val vehicle = CustomerVehicle(
                    id = existingVehicle?.id ?: 0,
                    customerId = customerId,
                    brand = brand.trim(),
                    model = model.trim(),
                    color = color.trim(),
                    plate = normalizedPlate,
                    plateType = plateType,
                    category = category,
                    parkingSpot = parkingSpot?.trim()?.takeIf { it.isNotBlank() },
                    isPrimary = existingVehicle?.isPrimary ?: isFirstVehicle,
                    createdAt = existingVehicle?.createdAt ?: now
                )

                if (vehicleId == null) {
                    saveVehicleUseCase(vehicle)
                } else {
                    updateVehicleUseCase(vehicle)
                }

                _uiState.value = _uiState.value.copy(
                    successMessage = if (vehicleId == null) "Veículo salvo" else "Veículo atualizado",
                    saveAndAddAnother = addAnother,
                    selectedVehicle = null
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Erro ao salvar veículo")
            }
        }
    }

    fun deleteVehicle(vehicleId: Int) {
        viewModelScope.launch {
            try {
                deleteVehicleUseCase(vehicleId)
                _uiState.value = _uiState.value.copy(successMessage = "Veículo removido")
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Erro ao remover veículo")
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, saveAndAddAnother = false)
    }

    private fun validatePlate(plate: String, type: PlateType): String? {
        return when (type) {
            PlateType.MERCOSUL ->
                if (!Regex("^[A-Z]{3}[0-9][A-Z][0-9]{2}$").matches(plate))
                    "Placa Mercosul inválida (ex: ABC1D23)"
                else null
            PlateType.OUTRA ->
                if (!Regex("^[A-Z]{3}-?[0-9]{4}$").matches(plate))
                    "Placa inválida (ex: ABC-1234 ou ABC1234)"
                else null
        }
    }
}

