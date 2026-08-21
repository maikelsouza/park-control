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

    var first30Minutes by mutableStateOf("500")
        private set

    var hourlyRate by mutableStateOf("700")
        private set

    init {
        // collect stored settings and update UI state
        viewModelScope.launch {
            getParkingConfigUseCase().collect { config ->
                // format as digits representing cents, e.g. 5.00 -> "500"
                first30Minutes = config.first30MinutesPrice.toCentsDigits()
                hourlyRate = config.pricePerHour.toCentsDigits()
            }
        }
    }

    fun onFirst30MinutesChange(value: String) {
        first30Minutes = value.filter(Char::isDigit).take(11)
    }

    fun onHourlyRateChange(value: String) {
        hourlyRate = value.filter(Char::isDigit).take(11)
    }

    fun saveSettings() {
        viewModelScope.launch {
            val first30 = first30Minutes.filter(Char::isDigit).toLongOrNull()?.let { it / 100.0 }
            val hourly = hourlyRate.filter(Char::isDigit).toLongOrNull()?.let { it / 100.0 }

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

    private fun Double.toCentsDigits(): String {
        val cents = Math.round(this * 100.0)
        return cents.toString()
    }
}