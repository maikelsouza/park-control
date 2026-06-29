package com.parkcontrol.features.settings.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.core.domain.model.ParkingConfig
import com.parkcontrol.core.domain.usecase.GetParkingConfigUseCase
import com.parkcontrol.core.domain.usecase.SaveParkingConfigUseCase
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsViewModel(
    private val getParkingConfigUseCase: GetParkingConfigUseCase,
    private val saveParkingConfigUseCase: SaveParkingConfigUseCase,
    application: Application
) : AndroidViewModel(application) {

    // Constructor for backward compatibility (lazy initialization)
    constructor(application: Application) : this(
        getParkingConfigUseCase = CoreDependencies.createGetParkingConfigUseCase(application),
        saveParkingConfigUseCase = CoreDependencies.createSaveParkingConfigUseCase(application),
        application = application
    )

    var first30Minutes by mutableStateOf("5.00")
        private set

    var hourlyRate by mutableStateOf("7.00")
        private set

    init {
        // collect stored settings and update UI state
        viewModelScope.launch {
            getParkingConfigUseCase().collect { config ->
                // format as decimal with dot
                first30Minutes = String.format(Locale.US, "%.2f", config.first30MinutesPrice)
                hourlyRate = String.format(Locale.US, "%.2f", config.pricePerHour)
            }
        }
    }

    fun onFirst30MinutesChange(value: String) {
        first30Minutes = value
    }

    fun onHourlyRateChange(value: String) {
        hourlyRate = value
    }

    fun saveSettings() {
        viewModelScope.launch {
            val first30 = first30Minutes.replace(',', '.').toDoubleOrNull()
            val hourly = hourlyRate.replace(',', '.').toDoubleOrNull()

            if (first30 != null && hourly != null) {
                // Validation
                val safeFirst30 = if (first30 >= 0.0) first30 else 0.0
                val safeHourly = if (hourly >= 0.0) hourly else 0.0
                val safeTolerance = 30 // default tolerance in minutes

                // Create config and save via use case
                val config = ParkingConfig(
                    first30MinutesPrice = safeFirst30,
                    pricePerHour = safeHourly,
                    toleranceMinutes = safeTolerance
                )
                saveParkingConfigUseCase(config)
            }
        }
    }
}