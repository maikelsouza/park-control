package com.parkcontrol.features.parking.ui

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.usecase.FilterParkedVehiclesUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ParkedVehiclesViewModel(
    application: Application,
    private val filterParkedVehiclesUseCase: FilterParkedVehiclesUseCase = FilterParkedVehiclesUseCase()
) : AndroidViewModel(application) {

    private val observeParkingRecordsUseCase by lazy {
        CoreDependencies.createObserveParkingRecordsUseCase(application)
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val _plateFilter = mutableStateOf("")
    val plateFilter: State<String> = _plateFilter

    private val _startDateFilter = mutableStateOf("")
    val startDateFilter: State<String> = _startDateFilter

    private val _endDateFilter = mutableStateOf("")
    val endDateFilter: State<String> = _endDateFilter

    private val _filterError = mutableStateOf<String?>(null)
    val filterError: State<String?> = _filterError

    private val _filteredRecords = mutableStateOf<List<ParkingRecord>>(emptyList())
    val filteredRecords: State<List<ParkingRecord>> = _filteredRecords

    private var allRecords: List<ParkingRecord> = emptyList()

    init {
        viewModelScope.launch {
            observeParkingRecordsUseCase().collect { records ->
                allRecords = records
                applyFilters()
            }
        }
    }

    fun updatePlateFilter(value: String) {
        _plateFilter.value = value.uppercase()
    }

    fun updateStartDateFilter(value: String) {
        _startDateFilter.value = value
    }

    fun updateEndDateFilter(value: String) {
        _endDateFilter.value = value
    }

    fun clearFilters() {
        _plateFilter.value = ""
        _startDateFilter.value = ""
        _endDateFilter.value = ""
        _filterError.value = null
        applyFilters()
    }

    fun applyFilters() {
        _filterError.value = null

        val startDate = parseDateOrNull(_startDateFilter.value, "Data inicial invalida. Use dd/MM/yyyy")
        if (_filterError.value != null) return

        val endDate = parseDateOrNull(_endDateFilter.value, "Data final invalida. Use dd/MM/yyyy")
        if (_filterError.value != null) return

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            _filterError.value = "A data final deve ser maior ou igual a data inicial"
            return
        }

        _filteredRecords.value = filterParkedVehiclesUseCase(
            records = allRecords,
            plateQuery = _plateFilter.value,
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun parseDateOrNull(value: String, errorMessage: String): LocalDate? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return null

        return try {
            LocalDate.parse(trimmed, dateFormatter)
        } catch (_: Exception) {
            _filterError.value = errorMessage
            null
        }
    }
}


