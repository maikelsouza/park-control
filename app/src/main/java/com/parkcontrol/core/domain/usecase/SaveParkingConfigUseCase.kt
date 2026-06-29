package com.parkcontrol.core.domain.usecase

import com.parkcontrol.core.domain.model.ParkingConfig
import com.parkcontrol.core.domain.repository.ParkingConfigRepository

/**
 * Use case for updating parking configuration.
 * Shared across features that modify parking settings (e.g., Settings feature).
 */
class SaveParkingConfigUseCase(
    private val repository: ParkingConfigRepository
) {
    suspend operator fun invoke(config: ParkingConfig) {
        repository.saveParkingConfig(config)
    }

    suspend fun saveFirst30MinutesPrice(price: Double) {
        repository.saveFirst30MinutesPrice(price)
    }

    suspend fun savePricePerHour(rate: Double) {
        repository.saveHourlyRate(rate)
    }

    suspend fun saveToleranceMinutes(minutes: Int) {
        repository.saveToleranceMinutes(minutes)
    }
}

