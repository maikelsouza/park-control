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
import kotlin.math.roundToInt

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

    var pixKey by mutableStateOf("")
        private set

    var first30MinutesError by mutableStateOf<String?>(null)
        private set

    var hourlyRateError by mutableStateOf<String?>(null)
        private set

    init {
        // collect stored settings and update UI state
        viewModelScope.launch {
            getParkingConfigUseCase().collect { config ->
                // format as digits representing cents, e.g. 5.00 -> "500"
                first30Minutes = config.first30MinutesPrice.toCentsDigits()
                hourlyRate = config.pricePerHour.toCentsDigits()
                pixKey = config.pixKey
            }
        }
    }

    fun onFirst30MinutesChange(value: String) {
        first30Minutes = value.filter(Char::isDigit).take(11)
        first30MinutesError = null
    }

    fun onHourlyRateChange(value: String) {
        hourlyRate = value.filter(Char::isDigit).take(11)
        hourlyRateError = null
    }

    fun onPixKeyChange(value: String) {
        pixKey = value
    }

    fun saveSettings() {
        val first30 = first30Minutes.filter(Char::isDigit).toLongOrNull()?.let { it / 100.0 }
        val hourly = hourlyRate.filter(Char::isDigit).toLongOrNull()?.let { it / 100.0 }

        first30MinutesError = if (first30 == null || first30 < 0.0) {
            "Informe o valor dos 30 minutos"
        } else {
            null
        }

        hourlyRateError = if (hourly == null || hourly <= 0.0) {
            "Informe o valor por hora"
        } else {
            null
        }

        if (first30MinutesError != null || hourlyRateError != null) {
            return
        }

        viewModelScope.launch {
            val safeFirst30 = first30!!
            val safeHourly = hourly!!
            val safeTolerance = 30 // default tolerance in minutes

            // Create config and save via use case
            val config = ParkingConfig(
                first30MinutesPrice = safeFirst30,
                pricePerHour = safeHourly,
                toleranceMinutes = safeTolerance,
                pixKey = pixKey
            )
            saveParkingConfigUseCase(config)
        }
    }

    private fun Double.toCentsDigits(): String {
        val cents = (this * 100.0).roundToInt()
        return cents.toString()
    }
}