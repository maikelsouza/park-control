package com.parkcontrol.features.parking.ui

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.ParkingStatus
import com.parkcontrol.features.parking.domain.usecase.CalculateParkingPriceUseCase
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ParkingViewModel(
    application: Application,
    private val calculateParkingPrice:
        CalculateParkingPriceUseCase = CalculateParkingPriceUseCase()
) : AndroidViewModel(application) {

    // Get use case from core dependencies (shared across features)
    private val getParkingConfigUseCase by lazy {
        CoreDependencies.createGetParkingConfigUseCase(application)
    }

    private val observeParkingRecordsUseCase by lazy {
        CoreDependencies.createObserveParkingRecordsUseCase(application)
    }

    private val saveParkingRecordUseCase by lazy {
        CoreDependencies.createSaveParkingRecordUseCase(application)
    }

    private val updateParkingRecordUseCase by lazy {
        CoreDependencies.createUpdateParkingRecordUseCase(application)
    }

    // Initialize state variables before init block
    private val _licensePlate = mutableStateOf("")
    val licensePlate: State<String> = _licensePlate

    private val _phone = mutableStateOf("")
    val phone: State<String> = _phone

    private val _parkingRecords = mutableStateOf<List<ParkingRecord>>(emptyList())
    val parkingRecords: State<List<ParkingRecord>> = _parkingRecords

    private val _first30MinutesPrice = mutableStateOf("5.00")
    val first30MinutesPrice: State<String> = _first30MinutesPrice

    private val _pricePerHour = mutableStateOf("7.00")
    val pricePerHour: State<String> = _pricePerHour

    init {
        // load persisted parking config and keep UI state in sync
        viewModelScope.launch {
            getParkingConfigUseCase().collect { config ->
                _first30MinutesPrice.value = String.format(java.util.Locale.US, "%.2f", config.first30MinutesPrice)
                _pricePerHour.value = String.format(java.util.Locale.US, "%.2f", config.pricePerHour)
            }
        }

        viewModelScope.launch {
            observeParkingRecordsUseCase().collect { records ->
                _parkingRecords.value = records
            }
        }
    }

    fun updateLicensePlate(plate: String) {
        _licensePlate.value = plate.uppercase()
    }

    fun updatePhone(phone: String) {
        _phone.value = phone
    }

    fun registerEntry() {
        val normalizedPlate = _licensePlate.value.trim().uppercase()
        if (normalizedPlate.isEmpty()) return

        val newRecord = ParkingRecord(
            licensePlate = normalizedPlate,
            phone = _phone.value.trim(),
            entryTime = LocalDateTime.now(),
            status = ParkingStatus.ESTACIONADO
        )

        viewModelScope.launch {
            saveParkingRecordUseCase(newRecord)
        }

        _licensePlate.value = ""
        _phone.value = ""
    }

    fun registerExit(record: ParkingRecord) {

        val exitTime = LocalDateTime.now()

        val first30MinutesPrice = _first30MinutesPrice.value.toDoubleOrNull()
                ?: 5.0

        val pricePerHour = _pricePerHour.value.toDoubleOrNull()
                ?: 7.0

        val amountPaid = calculateParkingPrice(
                entry = record.entryTime,
                exit = exitTime,
                first30MinutesPrice = first30MinutesPrice,
                pricePerHour = pricePerHour
        )

        val updatedRecord = record.copy(
            exitTime = exitTime,
            status = ParkingStatus.FINALIZADO,
            amountPaid = amountPaid
        )

        viewModelScope.launch {
            updateParkingRecordUseCase(updatedRecord)
        }
    }

    fun registerLastExit() {
        val lastRecord = getLastRecord()

        if (lastRecord != null &&
            lastRecord.status == ParkingStatus.ESTACIONADO
        ) {
            registerExit(lastRecord)
        }
    }

    fun getLastRecord(): ParkingRecord? = _parkingRecords.value.firstOrNull()

}