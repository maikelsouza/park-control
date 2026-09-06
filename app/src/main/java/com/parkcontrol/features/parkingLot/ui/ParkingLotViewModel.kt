package com.parkcontrol.features.parkingLot.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.core.domain.model.ParkingLotInfo
import com.parkcontrol.core.domain.usecase.GetParkingLotInfoUseCase
import com.parkcontrol.core.domain.usecase.SaveParkingLotInfoUseCase
import com.parkcontrol.core.ui.masks.onlyPhoneDigits
import com.parkcontrol.core.ui.masks.onlyZipCodeDigits
import kotlinx.coroutines.launch

/**
 * ViewModel for the parking lot registration screen.
 * Manages a single record (name, phone, address) that can be
 * created/edited, but never deleted or deactivated.
 *
 * The address fields mirror the same structure and required-field
 * rules used by the Agreement (Convênio) registration screen.
 */
class ParkingLotViewModel(
    private val getParkingLotInfoUseCase: GetParkingLotInfoUseCase,
    private val saveParkingLotInfoUseCase: SaveParkingLotInfoUseCase,
    application: Application
) : AndroidViewModel(application) {

    // Constructor for backward compatibility (lazy initialization)
    constructor(application: Application) : this(
        getParkingLotInfoUseCase = CoreDependencies.createGetParkingLotInfoUseCase(application),
        saveParkingLotInfoUseCase = CoreDependencies.createSaveParkingLotInfoUseCase(application),
        application = application
    )

    var name by mutableStateOf("")
        private set

    var phone by mutableStateOf("")
        private set

    var street by mutableStateOf("")
        private set

    var number by mutableStateOf("")
        private set

    var complement by mutableStateOf("")
        private set

    var neighborhood by mutableStateOf("")
        private set

    var city by mutableStateOf("")
        private set

    var state by mutableStateOf("")
        private set

    var zipCode by mutableStateOf("")
        private set

    var showValidation by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            getParkingLotInfoUseCase().collect { info ->
                name = info.name
                phone = info.phone
                street = info.street
                number = info.number
                complement = info.complement
                neighborhood = info.neighborhood
                city = info.city
                state = info.state
                zipCode = info.zipCode
            }
        }
    }

    fun onNameChange(value: String) {
        name = value
    }

    fun onPhoneChange(value: String) {
        phone = value.onlyPhoneDigits().take(11)
    }

    fun onStreetChange(value: String) {
        street = value.take(MAX_STREET_LENGTH)
    }

    fun onNumberChange(value: String) {
        number = value.take(MAX_NUMBER_LENGTH)
    }

    fun onComplementChange(value: String) {
        complement = value.take(MAX_COMPLEMENT_LENGTH)
    }

    fun onNeighborhoodChange(value: String) {
        neighborhood = value.take(MAX_NEIGHBORHOOD_LENGTH)
    }

    fun onCityChange(value: String) {
        city = value.take(MAX_CITY_LENGTH)
    }

    fun onStateChange(value: String) {
        state = value
    }

    fun onZipCodeChange(value: String) {
        zipCode = value.onlyZipCodeDigits().take(8)
    }

    fun isValid(): Boolean {
        return name.isNotBlank() &&
            phone.isNotBlank() &&
            street.isNotBlank() &&
            neighborhood.isNotBlank() &&
            city.isNotBlank() &&
            state.isNotBlank() &&
            zipCode.isNotBlank()
    }

    fun saveParkingLot(onSaved: () -> Unit) {
        showValidation = true
        if (!isValid()) return

        viewModelScope.launch {
            isSaving = true
            try {
                val info = ParkingLotInfo(
                    name = name.trim(),
                    phone = phone.onlyPhoneDigits().take(11),
                    street = street.trim(),
                    number = number.trim(),
                    complement = complement.trim(),
                    neighborhood = neighborhood.trim(),
                    city = city.trim(),
                    state = state.trim().uppercase().take(2),
                    zipCode = zipCode.onlyZipCodeDigits().take(8)
                )
                saveParkingLotInfoUseCase(info)
                isSaving = false
                successMessage = "Estacionamento salvo com sucesso"
                onSaved()
            } catch (_: Exception) {
                isSaving = false
                errorMessage = "Erro ao salvar estacionamento"
            }
        }
    }

    fun clearErrorMessage() {
        errorMessage = null
    }

    fun clearSuccessMessage() {
        successMessage = null
    }

    companion object {
        const val MAX_STREET_LENGTH = 150
        const val MAX_NUMBER_LENGTH = 10
        const val MAX_COMPLEMENT_LENGTH = 100
        const val MAX_NEIGHBORHOOD_LENGTH = 100
        const val MAX_CITY_LENGTH = 100
    }
}

